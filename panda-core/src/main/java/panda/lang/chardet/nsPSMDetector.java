package panda.lang.chardet;

import panda.lang.Arrays;

public abstract class nsPSMDetector {
	public static final int MAX_VERIFIERS = 16;

	nsVerifier[] mVerifier;
	nsEUCStatistics[] mStatisticsData;

	nsEUCSampler mSampler = new nsEUCSampler();
	byte[] mState = new byte[MAX_VERIFIERS];
	int[] mItemIdx = new int[MAX_VERIFIERS];

	int mItems;
	int mClassItems;

	boolean mDone;
	boolean mRunSampler;
	boolean mClassRunSampler;

	public nsPSMDetector() {
		initVerifiers(LangHint.ALL);
		Reset();
	}

	public nsPSMDetector(LangHint langFlag) {
		initVerifiers(langFlag);
		Reset();
	}

	public nsPSMDetector(int aItems, nsVerifier[] aVerifierSet, nsEUCStatistics[] aStatisticsSet) {
		mClassRunSampler = (aStatisticsSet != null);
		mStatisticsData = aStatisticsSet;
		mVerifier = aVerifierSet;

		mClassItems = aItems;
		Reset();
	}

	public void Reset() {
		mRunSampler = mClassRunSampler;
		mDone = false;
		mItems = mClassItems;

		for (int i = 0; i < mItems; i++) {
			mState[i] = 0;
			mItemIdx[i] = i;
		}

		mSampler.Reset();
	}

	protected void initVerifiers(LangHint langHint) {
		mVerifier = null;
		mStatisticsData = null;

		if (LangHint.TRADITIONAL_CHINESE.equals(langHint)) {
			mVerifier = new nsVerifier[] { 
					new nsUTF8Verifier(), 
					new nsBIG5Verifier(),
					new nsISO2022CNVerifier(), 
					new nsEUCTWVerifier(), 
					new nsCP1252Verifier(),
					new nsUCS2BEVerifier(), 
					new nsUCS2LEVerifier() 
				};

			mStatisticsData = new nsEUCStatistics[] { 
					null, 
					new Big5Statistics(), 
					null,
					new EUCTWStatistics(), 
					null, 
					null, 
					null 
				};
		}

		// ==========================================================
		else if (LangHint.KOREAN.equals(langHint)) {
			mVerifier = new nsVerifier[] { 
					new nsUTF8Verifier(), 
					new nsEUCKRVerifier(),
					new nsISO2022KRVerifier(), 
					new nsCP1252Verifier(), 
					new nsUCS2BEVerifier(),
					new nsUCS2LEVerifier() 
				};
		}

		// ==========================================================
		else if (LangHint.SIMPLIFIED_CHINESE.equals(langHint)) {
			mVerifier = new nsVerifier[] { 
					new nsUTF8Verifier(), 
					new nsGB2312Verifier(),
					new nsGB18030Verifier(), 
					new nsISO2022CNVerifier(), 
					new nsHZVerifier(),
					new nsCP1252Verifier(), 
					new nsUCS2BEVerifier(), 
					new nsUCS2LEVerifier() 
				};
		}

		// ==========================================================
		else if (LangHint.JAPANESE.equals(langHint)) {
			mVerifier = new nsVerifier[] { 
					new nsUTF8Verifier(),
					new nsSJISVerifier(), 
					new nsEUCJPVerifier(),
					new nsISO2022JPVerifier(), 
					new nsCP1252Verifier(), 
					new nsUCS2BEVerifier(), 
					new nsUCS2LEVerifier() 
				};
		}
		// ==========================================================
		else if (LangHint.CHINESE.equals(langHint)) {
			mVerifier = new nsVerifier[] { 
					new nsUTF8Verifier(), 
					new nsGB2312Verifier(),
					new nsGB18030Verifier(), 
					new nsBIG5Verifier(), 
					new nsISO2022CNVerifier(),
					new nsHZVerifier(), 
					new nsEUCTWVerifier(), 
					new nsCP1252Verifier(),
					new nsUCS2BEVerifier(), 
					new nsUCS2LEVerifier() 
				};

			mStatisticsData = new nsEUCStatistics[] { 
					null, 
					new GB2312Statistics(), 
					null,
					new Big5Statistics(), 
					null, 
					null, 
					new EUCTWStatistics(), 
					null, 
					null, 
					null 
				};
		}

		// ==========================================================
		else {
			mVerifier = new nsVerifier[] { 
					new nsUTF8Verifier(), 
					new nsSJISVerifier(),
					new nsEUCJPVerifier(), 
					new nsISO2022JPVerifier(), 
					new nsEUCKRVerifier(),
					new nsISO2022KRVerifier(), 
					new nsBIG5Verifier(), 
					new nsEUCTWVerifier(),
					new nsGB2312Verifier(), 
					new nsGB18030Verifier(), 
					new nsISO2022CNVerifier(),
					new nsHZVerifier(), 
					new nsCP1252Verifier(), 
					new nsUCS2BEVerifier(),
					new nsUCS2LEVerifier() 
				};

			mStatisticsData = new nsEUCStatistics[] { 
					null, 
					null, 
					new EUCJPStatistics(), 
					null,
					new EUCKRStatistics(), 
					null, 
					new Big5Statistics(), 
					new EUCTWStatistics(),
					new GB2312Statistics(), 
					null, 
					null, 
					null, 
					null, 
					null, 
					null 
				};
		}

		mClassRunSampler = (mStatisticsData != null);
		mClassItems = mVerifier.length;

	}

	protected abstract void Report(String charset);

	protected boolean HandleData(byte[] in, int len) {
		byte b, st;

		for (int i = 0; i < len; i++) {
			b = in[i];
			for (int j = 0; j < mItems;) {
				st = nsVerifier.getNextState(mVerifier[mItemIdx[j]], b, mState[j]);
				if (st == nsVerifier.eItsMe) {
					Report(mVerifier[mItemIdx[j]].charset());
					mDone = true;
					return mDone;
				}
				
				if (st == nsVerifier.eError) {
					mItems--;
					if (j < mItems) {
						System.arraycopy(mItemIdx, j + 1, mItemIdx, j, mItems - j);
						System.arraycopy(mState, j + 1, mState, j, mItems - j);
					}
				}
				else {
					mState[j++] = st;
				}
			}

			if (mItems <= 1) {
				if (1 == mItems) {
					Report(mVerifier[mItemIdx[0]].charset());
				}
				mDone = true;
				return mDone;
			}

			int nonUCS2Num = 0;
			int nonUCS2Idx = 0;

			for (int j = 0; j < mItems; j++) {
				if ((!(mVerifier[mItemIdx[j]].isUCS2()))
						&& (!(mVerifier[mItemIdx[j]].isUCS2()))) {
					nonUCS2Num++;
					nonUCS2Idx = j;
				}
			}

			if (1 == nonUCS2Num) {
				Report(mVerifier[mItemIdx[nonUCS2Idx]].charset());
				mDone = true;
				return mDone;
			}
		}

		if (mRunSampler) {
			Sample(in, len);
		}

		return mDone;
	}

	protected void DataEnd() {
		if (mDone == true) {
			return;
		}

		if (mItems == 2) {
			if ((mVerifier[mItemIdx[0]].charset()).equals("GB18030")) {
				Report(mVerifier[mItemIdx[1]].charset());
				mDone = true;
			}
			else if ((mVerifier[mItemIdx[1]].charset()).equals("GB18030")) {
				Report(mVerifier[mItemIdx[0]].charset());
				mDone = true;
			}
		}

		if (mRunSampler) {
			Sample(null, 0, true);
		}
	}

	protected void Sample(byte[] in, int len) {
		Sample(in, len, false);
	}

	protected void Sample(byte[] in, int len, boolean aLastChance) {
		int possibleCandidateNum = 0;
		int j;
		int eucNum = 0;

		for (j = 0; j < mItems; j++) {
			if (null != mStatisticsData[mItemIdx[j]])
				eucNum++;
			if ((!mVerifier[mItemIdx[j]].isUCS2())
					&& (!(mVerifier[mItemIdx[j]].charset()).equals("GB18030")))
				possibleCandidateNum++;
		}

		mRunSampler = (eucNum > 1);

		if (mRunSampler) {
			mRunSampler = mSampler.Sample(in, len);
			if (((aLastChance && mSampler.GetSomeData()) || mSampler.EnoughData())
					&& (eucNum == possibleCandidateNum)) {
				mSampler.CalFreq();

				int bestIdx = -1;
				int eucCnt = 0;
				float bestScore = 0.0f;
				for (j = 0; j < mItems; j++) {
					if ((null != mStatisticsData[mItemIdx[j]])
							&& (!(mVerifier[mItemIdx[j]].charset()).equals("Big5"))) {
						float score = mSampler.GetScore(
							mStatisticsData[mItemIdx[j]].mFirstByteFreq(),
							mStatisticsData[mItemIdx[j]].mFirstByteWeight(),
							mStatisticsData[mItemIdx[j]].mSecondByteFreq(),
							mStatisticsData[mItemIdx[j]].mSecondByteWeight());
						// System.out.println("FequencyScore("+mVerifier[mItemIdx[j]].charset()+")= "+
						// score);
						if ((0 == eucCnt++) || (bestScore > score)) {
							bestScore = score;
							bestIdx = j;
						} // if(( 0 == eucCnt++) || (bestScore > score ))
					} // if(null != ...)
				} // for
				if (bestIdx >= 0) {
					Report(mVerifier[mItemIdx[bestIdx]].charset());
					mDone = true;
				}
			} // if (eucNum == possibleCandidateNum)
		} // if(mRunSampler)
	}

	public String[] getProbableCharsets() {
		if (mItems <= 0) {
			return Arrays.EMPTY_STRING_ARRAY;
		}

		String ret[] = new String[mItems];
		for (int i = 0; i < mItems; i++) {
			ret[i] = mVerifier[mItemIdx[i]].charset();
		}
		return ret;
	}

	public String getProbableCharset() {
		if (mItems <= 0) {
			return null;
		}

		return mVerifier[mItemIdx[0]].charset();
	}

}
