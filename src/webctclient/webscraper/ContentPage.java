package webctclient.webscraper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** webct cource content page or folder view */
public class ContentPage {
	/** logger */
	static Logger logger = Logger.getLogger(ContentPage.class);
	
	/** content items */
	private LinkedList<ContentItem> items = new LinkedList<ContentItem>();
	
	/** course id */
	private long courseId;
	/** template id */
	private long templateId;
	/** title */
	private String title;
	
	
	/**
	 * @return the id
	 */
	public long getCourseId() {
		return courseId;
	}

	/**
	 * @return the templateId
	 */
	public long getTemplateId() {
		return templateId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/** cons parse page 
	 * @param string 
	 * @throws UnsupportedEncodingException */
	public ContentPage(WebPage page, String title) throws UnsupportedEncodingException {
		this.title = title;
		/*
<!-- Left -->
<td valign="top" align="left" width="42%">
<a href="javascript:submitLoad('2168993309031','URL_TYPE', true, false)"
title="Link opens in a new window"
>
<img src="/webct/urw/lc169594905001.tp169606253001/PresentationSettingsIconServlet/defaultIconIdentifier/URL_TYPE/41064" hspace="2" border="0" align="left" class=""
/></a>
<div class="orgtext">
<a href="javascript:submitLoad('2168993309031','URL_TYPE', true, false)"
title="Link opens in a new window"
>
Coursework 2 resources</a>
<div align="left">
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- organizerMap/displayIconLongDescription.jsp -->
<!-- applicationframework/CommonHeader.jspi -->
<!-- applicationframework/contextPath.jspi -->
The online resources to help with coursework 2.
</div>
</div>
<!-- Right -->
</td>
*/
		Pattern coursePattern = Pattern.compile("/lc([0-9]+)[.]tp([0-9]+)/");
		Pattern submitLoadPattern = Pattern.compile("submitLoad[(]'([0-9]+)','([A-Z_]*)', [^,)]*, [^,)]*[)]");
		Pattern imgPattern = Pattern.compile("<img src=[\"]([^\"]*)[\"]");
		Pattern doctypePattern = Pattern.compile("<!DOCTYPE [^>]*>");
		Pattern commentPattern = Pattern.compile("<!--((([^\\-]+)[\\-])+[\\-])+>");
		String text = page.getText();
		Matcher courseMatcher = coursePattern.matcher(text);
		if (courseMatcher.find()) {
			try {
				this.courseId = Long.parseLong(courseMatcher.group(1));
				this.templateId = Long.parseLong(courseMatcher.group(2));
			}
			catch (Exception e) {
				logger.error("Parsing course ids from page ("+courseMatcher.group()+")", e);
			}
		}
		Matcher submitLoadMatcher = submitLoadPattern.matcher(text);
		while (submitLoadMatcher.find()) {
			ContentItem item = new ContentItem();
			item.setCourseId(this.courseId);
			item.setTemplateId(this.templateId);
			String id = submitLoadMatcher.group(1);
			try {
				item.setId(Long.parseLong(id));				
			}
			catch (NumberFormatException nfe) {
				logger.error("ID not numeric: "+id);
				continue;
			}
			String type = submitLoadMatcher.group(2);
			item.setType(type);
			//logger.info("submitLoad "+id+" type "+type);
			int ix = submitLoadMatcher.end();
			String content = getElementContent(text, ix, "</a>");
			if (content!=null) {
				Matcher imgMatcher = imgPattern.matcher(content);
				if (imgMatcher.find())
					// assume icon
					continue;
				else {
					item.setTitle(content);
					//logger.info("Heading: "+content);
					// next div?
					int dix = text.indexOf("</a>", ix);
					if (dix>=0)
						dix = text.indexOf("<div ", dix);
					String description = null;
					if (dix>=0)
						description = getElementContent(text, dix, "</div>");
					if (description!=null) {
						// remove <!DOCTYPE ...> & <!-- ... -->
						description = doctypePattern.matcher(description).replaceAll("");
						description = commentPattern.matcher(description).replaceAll("");
						description = description.trim();
						item.setDescription(description);
						//logger.info("Description: "+description);
					}
					items.add(item);
				}
			}
		}
	}
	
	public LinkedList<ContentItem> getItems() {
		return items;
	}

	public void setItems(LinkedList<ContentItem> items) {
		this.items = items;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ContentPage [items=" + items + "]";
	}

	public static String getElementContent(String text, int ix, String endTag) {
		int start = text.indexOf(">", ix);
		if (start>=0) {
			int end = text.indexOf(endTag, start+1);
			if (end>=0) {
				String content = text.substring(start+1, end).trim();
				return content;
			}
		}	
		return null;
	}
//	/** test */
//	public static void main(String[] args) {
//		if (args.length<2) {
//			System.err.println("Usage: username [localfile] ...");
//			System.exit(-1);
//		}
//		try {
//			String user = args[0];
//			File userdir = new File(user);
//			userdir.mkdir();
//			
//			Cookies cookies = Login.webctLogin(args[0], PasswordUtils.getPassword(user));
//			logger.info("Login with cookies "+cookies.getRequestValue());
//			
//			for (int i=1; i<args.length; i++) {
//				File file = new File(args[i]);
//				logger.info("reading ContentPage from file "+file);
//				try {
//					ContentPage cp = new ContentPage(WebPage.read(file));
//					logger.info("Read: "+cp);
//					for (ContentItem item : cp.getItems()) {
//						logger.info("Item: "+item);
//						item.followItem(cookies, userdir);
//					}
//					
//					cp.dump();
//					
//				} catch (IOException e) {
//					logger.error("Reading "+file, e);
//				}
//			}
//		}
//		catch (Exception e) {
//			logger.error("Error", e);
//		}
//	}

	public void dump() {
		// TODO Auto-generated method stub
		dump(0);
	}

	void dump(int d) {
		// TODO Auto-generated method stub
		//for (int i=0; i<d; i++) 
		//	System.out.print("- ");
		for (ContentItem item : getItems()) {
			item.dump(d);
		}
	}
	public JSONObject toJson() throws JSONException {
		JSONObject jo = new JSONObject();
		jo.put("courseId", courseId);
		jo.put("templateId", templateId);
		jo.put("children", itemsToJson());
		return jo;
	}
	public JSONArray itemsToJson() throws JSONException {
		JSONArray ja = new JSONArray();
		for (ContentItem item : getItems()) {
			ja.put(item.toJson());
		}
		return ja;
	}

	public void writeIndex(File file) throws UnsupportedEncodingException, FileNotFoundException {
		logger.info("Write index to "+file);
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		pw.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		pw.println("<html><head>");
		//String title = "Course "+this.courseId+" (template "+this.templateId+")";
		pw.println("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		pw.println("<title>"+title+"</title>");
		pw.println("</head><body>");
		pw.println("<h1>"+title+"</h1>");
		for (ContentItem item : items) {
			if (ContentItem.PAGE_TYPE.equals(item.getType())) {
				pw.println("<h2><a href=\""+item.getFilename()+"\">"+item.getTitle()+"</a></h2>");
				pw.println("<p>[<a href=\""+item.getFilename()+"\">"+item.getFilename()+"</a>]</p>");
				pw.println("<div>"+item.getDescription()+"</div>");
				pw.println("<p>[type="+item.getType()+", id="+item.getId()+", tid="+item.getTemplateId()+", filename="+item.getFilename()+", fileId="+item.getFileId()+"]</p>");
				
			} else if (ContentItem.ORGANIZER_PAGE_TYPE.equals(item.getType()) ||
					ContentItem.TOC_TYPE.equals(item.getType())) {
				String folder = getSafeFileName(item.getTitle());
				pw.println("<h2><a href=\""+folder+"/index.html\">"+item.getTitle()+"</a></h2>");
				pw.println("<div>"+item.getDescription()+"</div>");
				pw.println("<p>[type="+item.getType()+", id="+item.getId()+", tid="+item.getTemplateId()+"]</p>");
				
			} else if (ContentItem.URL_TYPE.equals(item.getType())) {
				pw.println("<h2><a href=\""+item.getUrl()+"\">"+item.getTitle()+"</a></h2>");
				pw.println("<p>[<a href=\""+item.getUrl()+"\">"+item.getUrl()+"</a>]</p>");
				pw.println("<div>"+item.getDescription()+"</div>");			
				pw.println("<p>[type="+item.getType()+", id="+item.getId()+", tid="+item.getTemplateId()+", url="+item.getUrl()+"]</p>");
				pw.println("<p>["+item.getType()+" "+item.getId()+"]</p>");
			}
			else {
				pw.println("<h2>"+item.getTitle()+"</h2>");
				pw.println("<p>Currently unsupported type: "+item.getType()+"</p>");				
				pw.println("<p>[type="+item.getType()+", id="+item.getId()+", tid="+item.getTemplateId()+"]</p>");
			}
		}
		pw.println("</body></html>");
		pw.close();
	}

	private String getSafeFileName(String title) {
		// TODO Auto-generated method stub
		return title;
	}
}
