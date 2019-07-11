package panda.gems.admin.action;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import panda.app.action.BaseAction;
import panda.app.auth.Auth;
import panda.app.constant.AUTH;
import panda.codec.binary.Hex;
import panda.io.Streams;
import panda.lang.Strings;
import panda.mvc.annotation.At;
import panda.mvc.annotation.To;
import panda.mvc.annotation.param.Param;
import panda.mvc.view.Views;
import panda.util.crypto.Digests;
import panda.vfs.FileItem;


@At("${!!super_path|||'/super'}/hash")
@Auth(AUTH.SUPER)
@To(Views.SFTL)
public class HashAction extends BaseAction {
	/**
	 * execute
	 * @param s the string to hash
	 * @param f the file to hash
	 * @return Hash result map
	 * @throws Exception if an error occurs
	 */
	@At("")
	public Object execute(@Param("s") String s, @Param("f") FileItem f) throws Exception {
		if (Strings.isNotEmpty(s)) {
			return hashString(s);
		}
		
		if (f != null && f.isExists()) {
			return hashFile(f);
		}
		
		return null;
	}
	
	private Object hashString(String s) throws Exception {
		Map<String, String> m = new LinkedHashMap<String, String>();
		
		Set<String> as = Digests.getAvailableAlgorithms();
		for (String a : as) {
			m.put(a, Hex.encodeHexString(Digests.getDigest(a).digest(Strings.getBytesUtf8(s))));
		}

		return m;
	}
	
	private Object hashFile(FileItem f) throws Exception {
		Map<String, String> m = new LinkedHashMap<String, String>();
		
		Set<String> as = Digests.getAvailableAlgorithms();
		for (String a : as) {
			InputStream fis = f.open();
			try {
				m.put(a, Hex.encodeHexString(Digests.digest(Digests.getDigest(a), fis)));
			}
			finally {
				Streams.safeClose(fis);
			}
		}

		return m;
	}
}

