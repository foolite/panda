package panda.app.action.media;

import panda.app.auth.Auth;
import panda.app.constant.AUTH;
import panda.app.entity.Media;
import panda.mvc.annotation.At;

@At("${super_path}/media")
@Auth(AUTH.SUPER)
public class MediaBulkExAction extends MediaBulkAction {
	public String getMediaLink(Media m) {
		return Medias.getMediaLink(m);
	}
}