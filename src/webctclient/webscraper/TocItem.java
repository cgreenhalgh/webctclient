/**
 * 
 */
package webctclient.webscraper;

/** module (TOC) item
 * 
 * @author cmg
 *
 */
public class TocItem {

	private long courseId;
	private long templateId;
	/** the TOC */
	private long tocId;
	/** item (link) within TOC */
	private long tocLinkId;
	private String number;
	private String title;
	/** content item, e.g. page */
	private ContentItem item;
	
	/** cons */
	public TocItem() {		
	}

	public long getCourseId() {
		return courseId;
	}

	public void setCourseId(long courseId) {
		this.courseId = courseId;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public long getTocId() {
		return tocId;
	}

	public void setTocId(long tocId) {
		this.tocId = tocId;
	}

	public long getTocLinkId() {
		return tocLinkId;
	}

	public void setTocLinkId(long tocLinkId) {
		this.tocLinkId = tocLinkId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ContentItem getItem() {
		return item;
	}

	public void setItem(ContentItem item) {
		this.item = item;
	}
}
