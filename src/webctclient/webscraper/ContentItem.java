/**
 * 
 */
package webctclient.webscraper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
	private String filename;
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
	/** standard type */
	public static final String TOC_TYPE = "TOC_TYPE";
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
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
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
	public void followItem(Cookies cookies, File outputDir, File debugDir, boolean deep) {
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
			followOrganizerPage(cookies, outputDir, debugDir, deep);
		}
		else if (PAGE_TYPE.equals(type)) {
			followPage(cookies, outputDir, debugDir, deep);
		}
		else if (URL_TYPE.equals(type)) {
			followUrl(cookies, outputDir, debugDir, deep); 
		} 
		else if (TOC_TYPE.equals(type)) {
			followToc(cookies, outputDir, debugDir, deep); //"studentViewtoc.dowebct", "TOCId=");
		}
		else if ("DISCUSSION_CATEGORY_TYPE".equals(type))
			followOther(cookies, debugDir, "areaViewCSO.dowebct", "areaid=");
		else if ("DISCUSSION_TOPIC_TYPE".equals(type))
			followOther(cookies, debugDir, "topicViewCSO.dowebct", "topicid=");
		else if ("SCORM_TYPE".equals(type))
			followOther(cookies, debugDir, "displayscorm.dowebct", "scormId=");
		else if ("PROJECT_TYPE".equals(type))
			followOther(cookies, debugDir, "viewAssignedProject.dowebct", "projectId=");
		else if ("ASSESSMENT_TYPE".equals(type))
			followOther(cookies, debugDir, "displayAssessmentIntro.dowebct", "assessment=");
		else if ("MEDIA_COLLECTION_TYPE".equals(type))
			followOther(cookies, debugDir, "viewEntriesInCollection.dowebct", "collectionid=");
		else if ("CHAT_ROOM_TYPE".equals(type))
			followOther(cookies, debugDir, "enterChat.dowebct", "OrgRoomID=");
		else if ("SELF_ENROLLMENT_TYPE".equals(type))
			followOther(cookies, debugDir, "membergradebookGetSelfEnrollView.dowebct", "signupSheetId=");
		else if ("SYLLABUS_TYPE".equals(type))
			followOther(cookies, debugDir, "syllabusStudentView.dowebct", "componentId=");
		else if ("PROXY".equals(type))
			followOther(cookies, debugDir, "ptLaunch.dowebct", "id=");
		else 
			logger.info("Ignoring unsupported item type "+type);
	}
	private void followToc(Cookies cookies, File outputDir, File debugDir,
			boolean deep) {
		/*
		 <frame name="TOCMENUFRAME" title="Learning Module Table of Contents" scrolling="YES" src="/webct/urw/lc362180469001.tp362180493001/previewtocmenu.dowebct?updateBreadcrumb=&TOCId=2352911920061&TOCLinkId=2355956234031&nextLearningContextId=2355956234031" marginwidth="10" marginheight="0" frameborder="yes" /> 
		 */
		if (!deep)
			return;
		// TODO Auto-generated method stub
		WebPage p = followOther(cookies, debugDir, "studentViewtoc.dowebct", "TOCId=");
		if (p!=null) {
			try {
				// this produces a file which reloads to a different URL, see above
				Pattern linkPattern = Pattern.compile("src=[\"]/webct/urw/lc([0-9]+).tp([0-9]+)/previewtocmenu.dowebct[?](updateBreadcrumb=&)?TOCId=([0-9]+)&TOCLinkId=([0-9]+)(&nextLearningContextId=[0-9]+)?[\"]");
				Matcher m;
				m = linkPattern.matcher(p.getText());
				if (m.find()) 
				{
					String tocid = m.group(4), toclinkid = m.group(5);
					logger.info("TOCId "+tocid+", TOCLinkId "+toclinkid);
					String url = getUrl("previewtocmenu.dowebct", "TOCId="+tocid+"&TOCLinkId="+toclinkid);
					logger.info("toc page url: "+url);
					WebPage p2 = WebPage.download(url, cookies);
					File file2 = new File(debugDir, title+"_ref2.html");
					logger.info("save as "+file2);
					p2.write(file2);
					/*
				    var topmenu = new WebFXTree('test module');
    var menuComponent0 = new WebFXTreeItem('1 Heading 1');
    topmenu.add(menuComponent0);
    var menuComponent1 = new WebFXTreeItem('2 1.1 Course Outline','javascript:go(2355956234031,2352911920061)');
    topmenu.add(menuComponent1);
    var menuComponent2 = new WebFXTreeItem('2.1 another heading');
    menuComponent1.add(menuComponent2);
    var menuComponent3 = new WebFXTreeItem('2.2 ho hum','javascript:go(2355959838031,2352911920061)');
    menuComponent1.add(menuComponent3);
        document.write(topmenu);

function go(toclinkid, tocid) {
parent.location.href="/webct/urw/lc362180469001.tp362180493001/previewtoc.dowebct?updateBreadcrumb=false&resetBreadcrumb=false&TOCId="+tocid+"&TOCLinkId="+toclinkid+"#"+toclinkid;

				 */
					
					Pattern itemPattern = Pattern.compile("WebFXTreeItem[(][']([0-9]+([.][0-9]+)*)[ \\t]*([^']*)['](,'javascript:go[(]([0-9]+),([0-9]+)[)])?");
					Matcher m2 = itemPattern.matcher(p2.getText());
					while (m2.find()) {
						String number = m2.group(1);
						String text = m2.group(3);
						String itemtoclinkid = m2.group(5);
						logger.info("Item "+number+": '"+text+"' - linkid="+itemtoclinkid);
						if (itemtoclinkid==null)
							continue;
						try {
							String url3 = getUrl("previewtoc.dowebct", "TOCId="+tocid+"&TOCLinkId="+itemtoclinkid);
							logger.info("toc page url: "+url3);
							WebPage p3 = WebPage.download(url3, cookies);
							File file3 = new File(debugDir, title+"_"+text+"_ref2.html");
							logger.info("save as "+file3);
							p3.write(file3);

							/*
							 <frame name="TOCCONTENTFRAME" title="Learning Module Content" src="/webct/urw/lc362180469001.tp362180493001/displayContentPage.dowebct?updateBreadcrumb=false&pageID=2355956232031&TOCId=2352911920061&TOCLinkId=2355956234031&nextLearningContextId=2355959821031&displayBCInsideFrame=true" marginwidth="8" marginheight="4" scrolling="YES" frameborder="yes" />
							 */
						}
						catch (Exception e) {
							logger.error("following TOC item page "+itemtoclinkid, e);							
						}
					}
				}			
			} catch (Exception e) {
				logger.error("following TOC page "+this, e);
			}
		}
	}
	private void followPage(Cookies cookies, File outputDir, File debugDir, boolean deep) {
		
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
			File file = new File(debugDir, title+"_ref.html");
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

				// don't download here for now - use files API?!
				String url2 = getUrl("/RelativeResourceManager", "contentID="+contentID);
				logger.info("resource url?: "+url2);
				WebPage p2 = null;
				if (deep) 
					p2 = WebPage.download(url2, cookies, url);
				// file extension?!
				if (filename==null) {
					if (p2==null)
						p2 = WebPage.check(url2, cookies, url);

					String contentType = p2.getContentType();
					int ix = contentType.indexOf(";");
					if(ix>=0)
						contentType = contentType.substring(0, ix);
					contentType = contentType.trim();
					String extension = getFileExtension(contentType);
					this.filename = title+extension;
				}
				if (deep) {
					File file2 = new File(outputDir, filename);
					logger.info("save as "+file2);
					p2.write(file2);
				}
			}
		}
		catch (Exception e) {
			logger.error("following organizer page "+this, e);
		}
	}
	private String getFileExtension(String contentType) {
		if(contentType.equals("application/pdf"))
			return ".pdf";
		if(contentType.equals("application/postscript"))
			return ".ps";
		if(contentType.equals("application/zip"))
			return ".zip";
		if(contentType.equals("application/x-gzip"))
			return ".gzip";
		if(contentType.equals("application/xhtml+xml"))
			return ".xhtml";
		if(contentType.equals("application/ogg"))
			return ".ogg";
		if(contentType.equals("audio/mp4"))
			return ".mp4";
		if(contentType.equals("audio/mpeg"))
			return ".mpeg";
		if(contentType.equals("audio/ogg"))
			return ".ogg";
		if(contentType.equals("audio/x-ms-wma"))
			return ".wma";
		if(contentType.equals("audio/vnd.wave"))
			return ".wav";
		if(contentType.equals("image/gif"))
			return ".gif";
		if(contentType.equals("image/jpeg"))
			return ".jpg";
		if(contentType.equals("image/pjpeg"))
			return ".jpg";
		if(contentType.equals("image/png"))
			return ".png";
		if(contentType.equals("image/svg+xm"))
			return ".svg";
		if(contentType.equals("image/tiff"))
			return ".tiff";
		if(contentType.equals("image/vnd.microsoft.icon"))
			return ".ico";
		if(contentType.equals("model/vrml"))
			return ".vrml";
		if(contentType.equals("text/css"))
			return ".css";
		if(contentType.equals("text/csv"))
			return ".csv";
		if(contentType.equals("text/html"))
			return ".html";
		if(contentType.equals("text/plain"))
			return ".txt";
		if(contentType.equals("text/xml"))
			return ".xml";
		if(contentType.equals("video/mpeg"))
			return ".mpg";
		if(contentType.equals("video/mp4"))
			return ".mp4";
		if(contentType.equals("video/ogg"))
			return ".ogg";
		if(contentType.equals("video/quicktime"))
			return ".mov";
		if(contentType.equals("video/webm"))
			return ".webm";
		if(contentType.equals("video/x-ms-wmv"))
			return ".wmv";
//		if(contentType.equals("application/vnd.oasis.opendocument.text"))
//			return ".";
		if(contentType.equals("application/vnd.ms-excel"))
			return ".xls";
		if(contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
			return ".xlsx";
		if(contentType.equals("application/vnd.ms-powerpoint"))
			return ".ppt";
		if(contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation"))
			return ".pptx";
		if(contentType.equals("application/msword"))
			return ".doc";
		if(contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
			return ".docx";
		if(contentType.equals("application/vnd.google-earth.kml+xml"))
			return ".kml";
		if(contentType.equals("application/x-dvi"))
			return ".dvi";
		if(contentType.equals("application/x-latex"))
			return ".latex";
		if(contentType.equals("application/x-font-ttf:"))
			return ".ttf";
		if(contentType.equals("application/x-shockwave-flash"))
			return ".swf";
		if(contentType.equals("application/x-stuffit"))
			return ".stuffit";
		if(contentType.equals("application/x-rar-compressed"))
			return ".rar";
		if(contentType.equals("application/x-tar"))
			return ".tar";
//		if(contentType.equals(""))
//			return ".";
		// TODO - more options!
		logger.info("Unknown file extension for "+contentType);
		return "."+contentType.replace("/", ".");
	}
	private void followUrl(Cookies cookies, File outputDir, File debugDir, boolean deep) {
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
			File file = new File(debugDir, title+"_ref.html");
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
	private void followOrganizerPage(Cookies cookies, File outputDir, File debugDir, boolean deep) {
		// TODO Auto-generated method stub
/*
  studentCourseView.dowebct', 'displayinfo='+orgID+',ORGANIZER_PAGE_TYPE

 */
		if (!deep)
			return;
		try {
			String url = getUrl("studentCourseView.dowebct", "displayinfo="+id+",ORGANIZER_PAGE_TYPE");
			logger.info("organizer url: "+url);
			WebPage p = WebPage.download(url, cookies);
			File file = new File(debugDir, title+".html");
			logger.info("save as "+file);
			p.write(file);
			
			File subdir = new File(outputDir, title);
			subdir.mkdir();
			// recurse
			try {
				ContentPage cp = new ContentPage(WebPage.read(file), this.title);
				logger.info("Read: "+cp);
				for (ContentItem item : cp.getItems()) {
					logger.info("Item: "+item);
					item.followItem(cookies, subdir, debugDir, false);
				}
				this.contentPage = cp;
				cp.writeIndex(new File(subdir,"index.html"));
				if (deep) {
					for (ContentItem item : cp.getItems()) {
						logger.info("Item: "+item);
						item.followItem(cookies, subdir, debugDir, deep);
					}
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
	private WebPage followOther(Cookies cookies, File debugDir, String path, String queryPrefix) {
		return followOther(cookies, debugDir, path, queryPrefix, null);
	}
	private WebPage followOther(Cookies cookies, File debugDir, String path, String queryPrefix, String fragment) {
		try {
			String url = getUrl(path, queryPrefix+id);
			if (fragment!=null)
				url = url+'#'+fragment;
			logger.info("URL ref url: "+url);
			WebPage p = WebPage.download(url, cookies);
			File file = new File(debugDir, title+"_ref.html");
			logger.info("save as "+file);
			p.write(file);
			// ??
			return p;
		}
		catch (Exception e) {
			logger.error("following other page "+this, e);
		}
		return null;
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