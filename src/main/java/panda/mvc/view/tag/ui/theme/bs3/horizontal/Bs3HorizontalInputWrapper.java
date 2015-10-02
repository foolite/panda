package panda.mvc.view.tag.ui.theme.bs3.horizontal;

import java.io.IOException;

import panda.lang.Strings;
import panda.mvc.view.tag.ui.Div;
import panda.mvc.view.tag.ui.InputUIBean;
import panda.mvc.view.tag.ui.theme.RenderingContext;
import panda.mvc.view.tag.ui.theme.bs3.Bs3InputWrapper;

public abstract class Bs3HorizontalInputWrapper<T extends InputUIBean> extends Bs3InputWrapper<T> {
	protected String cssColLabel = "col-sm-3";
	protected String cssColInput = "col-sm-9";
	
	/**
	 * @param context context
	 */
	public Bs3HorizontalInputWrapper(RenderingContext context) {
		super(context);
	}

	@Override
	protected String getLabelClass() {
		return join(cssColLabel, "control-label");
	}

	@Override
	protected void renderInputDivBegin() throws IOException {
		write("<div");
		if (Strings.isNotEmpty(cssColInput)) {
			write(" class=\"");
			write(cssColInput);
			write('\"');
		}
		write('>');
	}
	
	@Override
	protected void renderInputDivEnd() throws IOException {
		etag("div");
	}
	
	//-------------------------------------------------------------------------------
	public static class GroupWrapper extends Bs3HorizontalInputWrapper<Div> {
		/**
		 * @param rc context
		 */
		public GroupWrapper(RenderingContext rc) {
			super(rc);
		}

		@Override
		public void renderStart() throws Exception {
			renderHeader();
			writeBefore();
			setInGroup(true);
		}

		@Override
		public void renderEnd() throws Exception {
			writeAfter();
			renderFooter();
			setInGroup(false);
		}
	}

	public static class ControlWrapper extends Bs3HorizontalInputWrapper {
		/**
		 * @param context context
		 */
		public ControlWrapper(RenderingContext context) {
			super(context);
		}

		protected void renderHeader() throws Exception {
			super.renderHeader();
			addCss("form-control");
		}
	}

	public static class StaticWrapper extends Bs3HorizontalInputWrapper {
		/**
		 * @param context context
		 */
		public StaticWrapper(RenderingContext context) {
			super(context);
		}

		protected void renderHeader() throws Exception {
			addCss("form-control-static");
			super.renderHeader();
		}
	}

	public static class ButtonWrapper extends Bs3HorizontalInputWrapper {
		/**
		 * @param context context
		 */
		public ButtonWrapper(RenderingContext context) {
			super(context);
		}
	}
}
