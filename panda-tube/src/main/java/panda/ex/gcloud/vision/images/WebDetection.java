package panda.ex.gcloud.vision.images;

import java.util.List;

/**
 * https://cloud.google.com/vision/docs/reference/rest/v1/images/annotate#WebDetection
 */
public class WebDetection {
	private List<WebEntity> webEntities;
	private List<WebImage> fullMatchingImages;
	private List<WebImage> partialMatchingImages;
	private List<WebPage> pagesWithMatchingImages;
	private List<WebImage> visuallySimilarImages;

	public List<WebEntity> getWebEntities() {
		return webEntities;
	}
	public void setWebEntities(List<WebEntity> webEntities) {
		this.webEntities = webEntities;
	}
	public List<WebImage> getFullMatchingImages() {
		return fullMatchingImages;
	}
	public void setFullMatchingImages(List<WebImage> fullMatchingImages) {
		this.fullMatchingImages = fullMatchingImages;
	}
	public List<WebImage> getPartialMatchingImages() {
		return partialMatchingImages;
	}
	public void setPartialMatchingImages(List<WebImage> partialMatchingImages) {
		this.partialMatchingImages = partialMatchingImages;
	}
	public List<WebPage> getPagesWithMatchingImages() {
		return pagesWithMatchingImages;
	}
	public void setPagesWithMatchingImages(List<WebPage> pagesWithMatchingImages) {
		this.pagesWithMatchingImages = pagesWithMatchingImages;
	}
	public List<WebImage> getVisuallySimilarImages() {
		return visuallySimilarImages;
	}
	public void setVisuallySimilarImages(List<WebImage> visuallySimilarImages) {
		this.visuallySimilarImages = visuallySimilarImages;
	}
}
