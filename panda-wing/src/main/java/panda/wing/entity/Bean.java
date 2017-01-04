package panda.wing.entity;

import panda.wing.constant.VC;

public abstract class Bean {
	/**
	 * copy properties from the specified object.
	 */
	public void copy(Bean src) {
	}

	/**
	 * is this data active
	 * @return true if bean is valid
	 */
	protected static boolean isActive(IStatus bean) {
		return bean.getStatus() != null && VC.STATUS_ACTIVE == bean.getStatus();
	}
	
	/**
	 * is this data disabled
	 * @return true if bean is disabled
	 */
	protected static boolean isDisabled(IStatus bean) {
		return bean.getStatus() != null && VC.STATUS_DISABLED == bean.getStatus();
	}
	
	/**
	 * is this data trashed
	 * @return true if bean is trashed
	 */
	protected static boolean isTrashed(IStatus bean) {
		return bean.getStatus() != null && VC.STATUS_TRASHED == bean.getStatus();
	}

	/**
	 * is this data changed
	 * @param lhs left bean
	 * @param rhs right bean
	 * @return true if lhs.utime != rhs.utime
	 */
	public static boolean isChanged(IUpdate lhs, IUpdate rhs) {
		if (lhs.getUusid() == null 
				|| lhs.getUtime() == null 
				|| rhs.getUusid() == null 
				|| rhs.getUtime() == null) {
			return false;
		}
		
		if (!lhs.getUusid().equals(rhs.getUusid())) {
			return true;
		}

		return (lhs.getUtime().getTime() / 1000) != (rhs.getUtime().getTime() / 1000);
	}
}
