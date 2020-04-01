package edu.carleton.comp4601.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.carleton.comp4601.analyzers.CommunityAnalyzer;
import edu.carleton.comp4601.analyzers.PreferenceAnalyzer;
import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.model.Community;
import edu.carleton.comp4601.model.Page;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.model.User;

//Main root path as per requirements of assignment
@Path("/rs")
public class ContextualAdvertisingSystem {

	private static final String PROJNAME = "Contextual Advertising System (CAS)";
	private static final String PARTNERS = "Alexander Nguyen & Redwan Wadud";
	private static boolean contextHit = false;
	private static boolean analyzer = false;

	// Display name as test that endpoints are working
	@GET
	public String nameOfSystem() {
		return PROJNAME;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtml() {
		String title = "<title>" + "COMP4601 - CAS" + "</title>";

		String projName = "<h1>" + PROJNAME + "</h1>";
		String partner = "<h2>" + PARTNERS + "</h2>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String bodyClose = "</h2></body>";
		String style = "<head><style>\r\n" + ".container { \r\n" + "  height: 100vh;\r\n" + "  position: relative;\r\n"
				+ "  text-align: center" + "}\r\n" + "\r\n" + ".center {\r\n" + "  margin: 0;\r\n"
				+ "  position: absolute;\r\n" + "  top: 50%;\r\n" + "  left: 50%;\r\n"
				+ "  -ms-transform: translate(-50%, -50%);\r\n" + "  transform: translate(-50%, -50%);\r\n"
				+ "}</style>\r\n" + "</head>";
		String divOpen = "<div class=\"container\">\r\n" + "  <div class=\"center\">";
		String divClose = "  </div>\r\n" + "</div>";

		return "<html> " + title + style + bodyOpen + divOpen + projName + partner + divClose + bodyClose + "</html> ";
	}

	@Path("context")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String contextProcessor() throws Exception {

		String title = "<head><title>" + "CAS - Context" + "</title>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String style = "<head><style>\r\n" + "table {\r\n" + "  border-collapse: collapse;\r\n"
				+ "border: 1px solid #ddd;\r\n" + "  width: 100%;\r\n" + "background-color: ivory;" + "}\r\n" + "\r\n"
				+ "th {\r\n" + "  padding: 8px;\r\n" + "color: white;\r\n" + "  text-align: center;\r\n"
				+ "  border: 1px solid black;\r\n" + "  background-color: #525d76;\r\n" + "}\r\n" + "\r\n" + "td {\r\n"
				+ "  padding: 8px;\r\n" + "  text-align: left;\r\n" + "  border: 1px solid #ddd;\r\n" + "}\r\n" + "\r\n"
				+ "tr:hover {background-color:#f5f5f5;}\r\n" + "</style></head>";
		String html = "<h1 style=\"padding: 15px;\" align=\"center\">Context</h1><table style= \"width:100%\"> <tr> <th>UserID</th> <th>Preferred Genre</th> <th>Movies Reviewed + User Score</th> <th>Community</th> </tr>";
		String ending = " </td>  </tr> </table></body></html>";

		try {
			DatabaseManager.getInstance();
			System.out.println("User collection loaded from database!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
					"Archive utility sequence incorrect - ensure archive is loaded & context is run first. Refer to readme.txt & try again.");
		}

		List<User> users = DatabaseManager.getInstance().getUsers();

		if (analyzer == false) {
			CommunityAnalyzer.analyze(users);
			PreferenceAnalyzer.analyze(users);
			analyzer = true;
		}

		for (int i = 0; i < users.size(); i++) {
			String userID = users.get(i).getUserId();

			String preferredGenre_load = users.get(i).getPreferredGenre();

			String preferredGenre = preferredGenre_load.substring(0, 1).toUpperCase()
					+ preferredGenre_load.substring(1);
			List<Review> reviews = DatabaseManager.getInstance().getUserReviews(userID);

			html = html + "<tr><td>" + userID + "</td><td>" + preferredGenre + "</td><td>";

			for (Review review : reviews)
				html = html + review.getPageId() + "(" + review.getScore() + ")" + ", ";

			html = html + "</td> <td> ";

			List<Community> communities = CommunityAnalyzer.getCommunities();

			for (Community community : communities) {
				List<User> members = community.getMembers();
				for (User member : members) {
					if (member.getUserId().equals(userID) && community.getCommunityName() == "Community 1")
						html = html + "Action Packers!";
					else if (member.getUserId().equals(userID) && community.getCommunityName() == "Community 2")
						html = html + "Funny People...";
					else if (member.getUserId().equals(userID) && community.getCommunityName() == "Community 3")
						html = html + "Horror Story";
				}
			}
		}

		// Put it all together
		html = title + style + bodyOpen + html + ending;

		contextHit = true;
		return html;
	}

	@Path("community")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String communityProcessor() throws Exception {

		String title = "<head><title>" + "CAS - Community" + "</title>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String style = "<head><style>\r\n" + "table {\r\n" + "  border-collapse: collapse;\r\n"
				+ "border: 1px #ddd;\r\n" + "  width: 100%;\r\n" + "background-color: ivory;" + "}\r\n" + "\r\n"
				+ "th {\r\n" + "  padding: 8px;\r\n" + "color: white;\r\n" + "  text-align: center;\r\n"
				+ "  border: 1px solid black;\r\n" + "  background-color: #525d76;\r\n" + "}\r\n" + "\r\n" + "td {\r\n"
				+ "  padding: 8px;\r\n" + "  text-align: left;\r\n" + "  border: 1px solid #ddd;\r\n" + "}\r\n" + "\r\n"
				+ "tr:hover {background-color:#f5f5f5;}\r\n" + "</style></head>";

		// Ensure context is run first as per assignment requirement
		String html = "";
		if (contextHit == false) {
			html = "Archive utility sequence incorrect - ensure archive is loaded & context is run first. Refer to readme.txt & try again.";
		} else {
			// HTML output and table config

			html = "<h1 style=\"padding: 15px;\" align=\"center\">Community</h1><table style= \"width:100%\"> <tr> <th>Community</th> <th>Community Members</th></tr>";
			String ending = " </td>  </tr> </table></body></html>";

			List<Community> communities = CommunityAnalyzer.getCommunities();

			// Now add each user profile to the table via the html string
			String actionCommunity = "";
			String comedyCommunity = "";
			String horrorCommunity = "";

			for (Community community : communities) {
				List<User> members = community.getMembers();
				for (User member : members) {
					if (community.getCommunityName() == "Community 1")
						actionCommunity = actionCommunity + member.getUserId() + ", ";
					else if (community.getCommunityName() == "Community 2")
						comedyCommunity = comedyCommunity + member.getUserId() + ", ";
					else if (community.getCommunityName() == "Community 3")
						horrorCommunity = horrorCommunity + member.getUserId() + ", ";
				}
			}

			html = html + "<tr><td>" + "Action Packers!" + "</td><td>" + actionCommunity + "</td></tr>";
			html = html + "<tr><td>" + "Funny ones..." + "</td><td>" + comedyCommunity + "</td></tr>";
			html = html + "<tr><td>" + "Horror Story" + "</td><td>" + horrorCommunity + "</td></tr>";

			html = title + style + bodyOpen + html + ending;

		}
		return html;
	}

	@Path("fetch/{user}/{page}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String fetchUserPageProcessor(@PathParam("user") String user, @PathParam("page") String page)
			throws Exception {

		String title = "<head><title>" + "CAS - Fetch" + "</title>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String style = "<head><style>\r\n" + "table {\r\n" + "  border-collapse: collapse;\r\n"
				+ "border: 1px #ddd;\r\n" + "  width: 100%;\r\n" + "background-color: ivory;" + "}\r\n" + "\r\n"
				+ "th {\r\n" + "  padding: 8px;\r\n" + "color: white;\r\n" + "  text-align: center;\r\n"
				+ "  border: 1px solid black;\r\n" + "  background-color: #525d76;\r\n" + "}\r\n" + "\r\n" + "td {\r\n"
				+ "  padding: 8px;\r\n" + "  text-align: left;\r\n" + "  border: 1px solid #ddd;\r\n" + "}\r\n" + "\r\n"
				+ "tr:hover {background-color:#f5f5f5;}\r\n" + "</style></head>";

		// Ensure context is run first as per assignment requirement
		String html = "";
		if (contextHit == false) {
			html = "Archive utility sequence incorrect - ensure archive is loaded & context is run first. Refer to readme.txt & try again.";
		} else {

			html = "<h1 style=\"padding: 15px;\" align=\"center\">Fetch</h1> <table style= \"width:100%\"><tr> <th colspan=\"2\"> User: "
					+ user + " - Page: " + page + "</th></tr>";

			// Now add each user profile to the table via the html string
			List<User> users = DatabaseManager.getInstance().getUsers();
			List<Review> movies = DatabaseManager.getInstance().getReviews();
			List<Page> pages = DatabaseManager.getInstance().getPages();

			String genre_load = "", genre = "", reviewString = ""; // Sort for movie we want
			float score = 0;

			for (int j = 0; j < movies.size(); j++) {

				// Find it
				if (movies.get(j).getPageId().equals(page) && movies.get(j).getUserId().equals(user)) {

					for (Page p : pages) {
						if (p.getPageId().equals(page)) {
							genre_load = p.getCategory();
							genre = genre_load.substring(0, 1).toUpperCase() + genre_load.substring(1);
						}
					}

					// Get data about movie
					score = movies.get(j).getScore();
					reviewString = movies.get(j).getContent();
				}
			}

			html = html + "<tr><td rowspan=\"2\">" + " <b>Movie: </b>" + page + " <br /><b>Average Rating: </b>" + score
					+ " <br /><b> Genre: </b>" + genre + " <br /><b>Reviews: </b>" + reviewString
					+ "</td><td style=\"color:white; text-align:center;\" bgcolor=\"#BEBEBE\"><b>Advertising</b></td></tr>";

			// Advertising
			String prefGen = "";
			for (int i = 0; i < users.size(); i++) {

				if (users.get(i).getUserId().toString().equals(user)) {
					prefGen = users.get(i).getPreferredGenre();
					Random rand = new Random();
					int ad = rand.nextInt(3) + 1;

					if (prefGen.equals("action")) {
						html = html + "<tr><td>"
								+ "<p><img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/actionAd"
								+ ad + ".png\" alt=\"Action 1 Ad\"/></p>";

						if (ad == 1) {
							html = html + "<p align=\"center\">"
									+ " <a href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									+ "0783226128"
									+ ".html\"> Click to check out the reviews (for the recommended movie)! </a>"
									+ "</p></td>";
						} else if (ad == 2) {
							html = html + "<p align=\"center\">"
									+ " <a href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									+ "0784010331"
									+ ".html\"> Click to check out the reviews (for the recommended movie)! </a>"
									+ "</p></td>";
						} else if (ad == 3) {
							html = html + "<p align=\"center\">"
									+ " <a href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									+ "6303212263"
									+ ".html\"> Click to check out the reviews (for the recommended movie)! </a>"
									+ "</p></td>";
						}

					} else if (prefGen.equals("horror")) {
						html = html + "<tr><td>"
								+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/horrorAd"
								+ ad + ".png\" alt=\"Horror 1 Ad\"/>";

						if (ad == 1) {
							html = html + "<p align=\"center\">"
									+ " <a href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									+ "6304240554"
									+ ".html\"> Click to check out the reviews (for the recommended movie)! </a>"
									+ "</p></td>";
						} else if (ad == 2) {
							html = html + "<p align=\"center\">"
									+ " <a href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									+ "B00004CJ2O"
									+ ".html\"> Click to check out the reviews (for the recommended movie)! </a>"
									+ "</p></td>";
						} else if (ad == 3) {
							html = html + "<p align=\"center\">"
									+ " <a href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									+ "B003EYVXUU"
									+ ".html\"> Click to check out the reviews (for the recommended movie)! </a>"
									+ "</p></td>";
						}
					} else if (prefGen.equals("comedy")) {
						html = html + "<tr><td>"
								+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/comedyAd"
								+ ad + ".png\" alt=\"Comedy 1 Ad\"/>";

						if (ad == 1) {
							html = html + "<p align=\"center\">"
									+ " <a href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									+ "B001KEHAI0"
									+ ".html\"> Click to check out the reviews (for the recommended movie)! </a>"
									+ "</p></td>";
						} else if (ad == 2) {
							html = html + "<p align=\"center\">"
									+ " <a href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									+ "B00004RM0J"
									+ ".html\"> Click to check out the reviews (for the recommended movie)! </a>"
									+ "</p></td>";
						} else if (ad == 3) {
							html = html + "<p align=\"center\">"
									+ " <a href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									+ "B00004CX8I"
									+ ".html\"> Click to check out the reviews (for the recommended movie)! </a>"
									+ "</p></td>";
						}
					}
				}
			}

			String ending = " </tr></table></body></html>";
			html = title + style + bodyOpen + html + ending;
		}
		return html;
	}

	// Display advertisements for given genre of user communities, refer to
	// /readme endpoint for help displaying desired content
	@Path("advertising/{genre}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String advertisingCategoryProcessor(@PathParam("genre") String genre) {

		String title = "<head><title>" + "CAS - Advertising" + "</title>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String style = "<head><style>\r\n" + "table {\r\n" + "  border-collapse: collapse;\r\n"
				+ "border: 1px #ddd;\r\n" + "  width: 100%;\r\n" + "background-color: ivory;" + "}\r\n" + "\r\n"
				+ "th {\r\n" + "  padding: 8px;\r\n" + "color: white;\r\n" + "  text-align: center;\r\n"
				+ "  border: 1px solid black;\r\n" + "  background-color: #525d76;\r\n" + "}\r\n" + "\r\n" + "td {\r\n"
				+ "  padding: 8px;\r\n" + "  text-align: left;\r\n" + "  border: 1px solid #ddd;\r\n" + "}\r\n" + "\r\n"
				+ "tr:hover {background-color:#f5f5f5;}\r\n" + "</style></head>";

		// HTML output and table config
		String html = "<h1 style=\"padding: 15px;\" align=\"center\">Advertising</h1><table style= \"width:100%\"><tr> <th> Genre </th> <th>Advertisement Samples</th> </tr>";
		String ending = "</table></body></html>";

		if (genre.equals("action")) {
			html = html + "<tr><td>" + " Action & Classics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/actionAd1.png\" alt=\"Action 1 Ad\"/>"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Action & Classics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/actionAd2.png\" alt=\"Action 2 Ad\"/>"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Action & Classics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/actionAd3.png\" alt=\"Action 3 Ad\"/>"
					+ "</td></tr>";
		} else if (genre.equals("horror")) {
			html = html + "<tr><td>" + " Fear Fanatics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/horrorAd1.png\" alt=\"Horror 1 Ad\"/>"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Fear Fanatics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/horrorAd2.png\" alt=\"Horror 2 Ad\"/>"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Fear Fanatics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/horrorAd3.png\" alt=\"Horror 3 Ad\"/>"
					+ "</td></tr>";
		} else if (genre.equals("comedy")) {
			html = html + "<tr><td>" + " Laugh Lovers" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/comedyAd1.png\" alt=\"Comedy 1 Ad\"/>"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Laugh Lovers" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/comedyAd2.png\" alt=\"Comedy 2 Ad\"/>"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Laugh Lovers" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/comedyAd2.png\" alt=\"Comedy 3 Ad\"/>"
					+ "</td></tr>";
		}

		html = title + style + bodyOpen + html + ending;

		return html;
	}

}
