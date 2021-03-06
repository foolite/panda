package panda.cast.castor;

import panda.cast.CastContext;
import panda.lang.Strings;
import panda.net.mail.EmailAddress;
import panda.net.mail.EmailException;


public class EmailAddressCastor extends AnySingleCastor<EmailAddress> {
	public EmailAddressCastor() {
		super(EmailAddress.class);
	}

	@Override
	protected EmailAddress castValue(Object value, CastContext cc) {
		if (value instanceof CharSequence) {
			String s = value.toString();
			if (Strings.isEmpty(s)) {
				return defaultValue();
			}
			
			try {
				return EmailAddress.parse(s);
			}
			catch (EmailException e) {
				return castError(value, cc, e);
			}
		}
		
		JavaBeanCastor<EmailAddress> jbc = new JavaBeanCastor<EmailAddress>(EmailAddress.class, cc.getCastors().getBeans());
		return jbc.cast(value, cc);
	}
}
