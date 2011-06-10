/**
 * 
 */
package webctclient.webscraper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import webctclient.WebctConstants;

public class ContentItem {
	/** logger */
	static Logger logger = Logger.getLogger(ContentItem.class);
	private long id;
	private String type;
	private String title;
	private String description;
	private long courseId;
	private long templateId;
	
	private String url;
	private ContentPage contentPage;
	private long fileId;
	
	/** standard type */
	public static final String ORGANIZER_PAGE_TYPE = "ORGANIZER_PAGE_TYPE";
	/** standard type */
	public static final String PAGE_TYPE = "PAGE_TYPE";
	/** standard type */
	public static final String URL_TYPE = "URL_TYPE";
	// hidden?
	/** cons */
	public ContentItem() {
		
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the courseId
	 */
	public long getCourseId() {
		return courseId;
	}
	/**
	 * @param courseId the courseId to set
	 */
	public void setCourseId(long courseId) {
		this.courseId = courseId;
	}
	/**
	 * @return the templateId
	 */
	public long getTemplateId() {
		return templateId;
	}
	/**
	 * @param templateId the templateId to set
	 */
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @return the contentPage
	 */
	public ContentPage getContentPage() {
		return contentPage;
	}
	/**
	 * @return the fileId
	 */
	public long getFileId() {
		return fileId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ContentItem [courseId=" + courseId + ", description="
				+ description + ", id=" + id + ", templateId=" + templateId
				+ ", title=" + title + ", type=" + type + "]";
	}
	public void followItem(Cookies cookies, File dir) {
		/*
function displayTool(actionUrl, query, openInNewWindow, windowName,linkId, officeFlag) {
...
[not scorm]
openPopupBox('/webct/urw/lc169594905001.tp169606253001/' + actionUrl + '?' + query, windowName, 'false');
...
function submitAreaDisplay(areaId, openInNewWindow, linkID){displayTool('areaViewCSO.dowebct', 'areaid='+areaId, openInNewWindow, areaId, linkID);}
function submitTopicDisplay(topicId, openInNewWindow, linkID) {displayTool('topicViewCSO.dowebct', 'topicid='+topicId, openInNewWindow, topicId, linkID);}
function submitPTDisplay(id, openInNewWindow, linkID) {displayTool('ptLaunch.dowebct', 'id='+id, openInNewWindow, id, linkID);}
function submitTocDisplay(tocID, openInNewWindow, linkID){displayTool('studentViewtoc.dowebct', 'TOCId='+tocID, openInNewWindow, tocID, linkID);}
function submitUrlDisplay(UrlID, openInNewWindow, linkID){displayTool('displayURLForQM.dowebct', 'URLId='+UrlID, openInNewWindow, UrlID, linkID);}
function submitScormDisplay(ScormID, openInNewWindow, linkID){displayTool('displayscorm.dowebct', 'scormId='+ScormID, true, ScormID, linkID);}
function submitPageDisplay(pageId, openInNewWindow, linkID, officeFlag){displayTool('displayContentPage.dowebct', 'pageID='+pageId, openInNewWindow, pageId, linkID, officeFlag);}
function submitOrganizerDisplay(orgID, linkID){displayTool('studentCourseView.dowebct', 'displayinfo='+orgID+',ORGANIZER_PAGE_TYPE', false, orgID, linkID);}
function submitProjectDisplay(projectId, openInNewWindow, linkID){displayTool('viewAssignedProject.dowebct', 'projectId='+projectId+'&returnURL=/courseFS.dowebct?tab=view', openInNewWindow, projectId, linkID);}
function submitAssessmentDisplay(assessmentId, openInNewWindow, linkID){displayTool('displayAssessmentIntro.dowebct', 'assessment='+assessmentId, openInNewWindow, assessmentId, linkID);}
function submitMediaCollectionDisplay(collectionId, openInNewWindow, linkID){displayTool('viewEntriesInCollection.dowebct', 'collectionid='+collectionId, openInNewWindow, collectionId, linkID);}
function submitChatRoomDisplay(roomId, openInNewWindow, linkID){displayTool('enterChat.dowebct', 'OrgRoomID='+roomId, openInNewWindow, roomId, linkID);}
function submitSelfEnrollDisplay(signupSheetId, openInNewWindow, orgId, linkID){displayTool('membergradebookGetSelfEnrollView.dowebct', 'signupSheetId='+signupSheetId+'&orgId='+orgId, openInNewWindow, signupSheetId, linkID);}

		 */
		if (ORGANIZER_PAGE_TYPE.equals(type)) {
			followOrganizerPage(cookies, dir);
		}
		else if (PAGE_TYPE.equals(type)) {
			followPage(cookies, dir);
		}
		else if (URL_TYPE.equals(type)) {
			followUrl(cookies, dir); 
		}
		else
			logger.info("Ignoring unsupported item type "+type);
	}
	private void followPage(Cookies cookies, File dir) {
		
/*
 'displayContentPage.dowebct', 'pageID='+pageId
->
  
  <script language="javascript">
window.location.href="/webct/urw/lc169594905001.tp169606253001//RelativeResourceManager?contentID=1903455085061";
</script>

 */
		try {
			String url = getUrl("displayContentPage.dowebct", "pageID="+id);
			logger.info("page url: "+url);
			WebPage p = WebPage.download(url, cookies);
			File file = new File(dir, title+"_ref.html");
			logger.info("save as "+file);
			p.write(file);

			// this produces a file which reloads to a different URL, see above
				Pattern linkPattern = Pattern.compile("window.location.href=[\"]([^\"\\?]*[\\?]contentID=([0-9]+))[\"]");
				Matcher m = linkPattern.matcher(p.getText());
				if (m.find()) 
				{
					logger.info("ID "+m.group(2)+" (URL: "+m.group(1)+")");

					String contentID = m.group(2);

					this.fileId = Long.parseLong(contentID);
					
					if (false) {
						// don't download here for now - use files API?!
						String url2 = getUrl("/RelativeResourceManager", "contentID="+contentID);
						logger.info("resource url?: "+url2);
						WebPage p2 = WebPage.download(url2, cookies, url);
						File file2 = new File(dir, title);
						logger.info("save as "+file2);
						p2.write(file2);
				}
			}
		}
		catch (Exception e) {
			logger.error("following organizer page "+this, e);
		}
	}
	private void followUrl(Cookies cookies, File dir) {
		// displayURLForQM.dowebct', 'URLId='+UrlID
		/*
<body bgcolor="#FFFFFF">
<form method="post">
<SCRIPT language='JavaScript'>
this.location= "http://www.nottingham.ac.uk/is/gateway/readinglists/local/displaylist?module=G54UBI";
</SCRIPT>
</form>

		 */
		try {
			String url = getUrl("displayURLForQM.dowebct", "URLId="+id);
			logger.info("URL ref url: "+url);
			WebPage p = WebPage.download(url, cookies);
			File file = new File(dir, title+"_ref.html");
			logger.info("save as "+file);
			p.write(file);
			// this produces a file which reloads to a different URL, see above
			Pattern linkPattern = Pattern.compile("this[.]location=[ ]*[\"]([^\"]*)[\"]");
			Matcher m = linkPattern.matcher(p.getText());
			if (m.find()) 
			{
				logger.info("URL: "+m.group(1));
				
				this.url = m.group(1);
			}
		}
		catch (Exception e) {
			logger.error("following organizer page "+this, e);
		}

	}
	private void followOrganizerPage(Cookies cookies, File dir) {
		// TODO Auto-generated method stub
/*
  studentCourseView.dowebct', 'displayinfo='+orgID+',ORGANIZER_PAGE_TYPE

 */
		try {
			String url = getUrl("studentCourseView.dowebct", "displayinfo="+id+",ORGANIZER_PAGE_TYPE");
			logger.info("organizer url: "+url);
			WebPage p = WebPage.download(url, cookies);
			File file = new File(dir, title+".html");
			logger.info("save as "+file);
			p.write(file);
			
			File subdir = new File(dir, title);
			subdir.mkdir();
			// recurse
			try {
				ContentPage cp = new ContentPage(WebPage.read(file));
				logger.info("Read: "+cp);
				for (ContentItem item : cp.getItems()) {
					logger.info("Item: "+item);
					item.followItem(cookies, subdir);
				}
				this.contentPage = cp;
			} catch (IOException e) {
				logger.error("Reading "+file, e);
			}
			
		}
		catch (Exception e) {
			logger.error("following organizer page "+this, e);
		}
	}
	private String getUrl(String action, String query) {
		return WebctConstants.PROTOCOL+"://"+WebctConstants.HOST+":"+WebctConstants.PORT+"/webct/urw/lc"+courseId+".tp"+templateId+"/"+action+"?"+query;
	}
	public void dump(int d) {
		// TODO Auto-generated method stub
		for (int i=0; i<d; i++) 
			System.out.print("- ");
		System.out.print(title+(description!=null && description.length()!=0 ? " ("+description+")" : ""));
		if (url!=null)
			System.out.println("-> "+url);
		else if (fileId!=0)
			System.out.println("-> File "+fileId);
		else if (contentPage!=null) {
			System.out.println(":");
			contentPage.dump(d+1);
		}
		else System.out.println(" "+type);
	}
	public JSONObject toJson() throws JSONException {
		JSONObject jo = new JSONObject();
		jo.put("id", id);
		jo.put("title", title);
		jo.put("type", type);
		if (description!=null)
			jo.put("description", description);
		if (url!=null)
			jo.put("url", url);
		if (fileId!=0)
			jo.put("fileId", fileId);
		if (contentPage!=null)
			jo.put("children", contentPage.itemsToJson());
		return jo;
	}
}