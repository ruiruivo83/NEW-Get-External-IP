import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class GET_EXTERNAL_IPV6 {

	// AUTHENTICATION VARIABLES
	static String user = "remote83";
	static String password = "1q2w3e4r";

	// VARIABLES
	static Connection conn = null; // java.sql.Connection
	static String url = "jdbc:mysql://www.ruiruivo.net:3306/Remote_DB";
	public static long oneBillion = 1000000000; // for Chronographe calculation

	static String dynamicHomeServerIP; // Stores new IP of the dynamic home server
	static String lastHomeServerIP; // Stores last retrieved Home server IP from Remote Database - for test purposes

	public static String DATE; // for current run date
	public static String IP; // for current IP need

	public static boolean ONLINE; // Declares Connection status
	public static String NEWLINESTRING; // Stores new line for log file

	static int timesleep = 10000; // 10000 / 1000 = 10 Seconds
	public static double PROGRAM_ELAPSED_TIME; // Final Chronographe elapsed time

	// Keeps program running while true
	static public boolean PROGRAMRUN = true;

	public static void main(String[] args) throws Exception {

		while (PROGRAMRUN = true) {

			// Start Chronographe
			long start = System.nanoTime(); // For Performance analyze

			// TEST for internet Connection
			internetConnectionTest(); // OK

			// TEST connection to remote DataBase
			dbConnectionTEST(); // OK

			// Get Dynamic Home server IP
			getDynamicHomeServerIP(); // OK

			// Add Dynamic Home server IP to mySQL DataBase
			addDynamicHomeServerIPtoDB(); // OK

			// Retrieve last HomeServerIP on remote DataBase
			retrieveLastHomeServerIP();

			// Print elapse Chronographe time
			double elapsedTime = System.nanoTime() - start;
			elapsedTime = elapsedTime / oneBillion;
			System.out.println("Completed in: " + elapsedTime);

			// Prints end Statement
			System.out.println("Next loop in: " + timesleep / 1000 + " Seconds.");
			System.out.println("_______________________________________________");

			// Main thread STOPS for x seconds and program restarts
			Thread.sleep(timesleep);

		}
	}

	// Tests for internet connection
	private static void internetConnectionTest() {
		ONLINE = false;
		// Verify Internet Connection
		try {
			URL url = new URL("http://www.google.com");
			URLConnection con = url.openConnection();
			con.getInputStream();
			ONLINE = true;
			System.out.println("- TEST - ONLINE OK");
		} catch (IOException e) {
			ONLINE = false;
			System.out.println("- TEST - NO CONNECTION TO INTERNET");
		}
	}

			
			
	private static void addDynamicHomeServerIPtoDB() {
		try {
			// TRY DATABASE connection
			String url = "jdbc:mysql://www.ruiruivo.net:3306/Remote_DB";
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, "remote83", "1q2w3e4r");
			System.out.println("- Database Connection ESTABLISHED OK");

			// SEND Data SQL QUERY into database
			Statement st = conn.createStatement();
			String SQLQuery = "INSERT INTO homeserver VALUES ('" + dynamicHomeServerIP.toString() + "')";
			st.executeUpdate(SQLQuery);			
			System.out.println("- Data send OK");

		} catch (Exception e) { // CATCH Connection error
			e.printStackTrace();
			System.out.println("- CANNOT ADD to DATABASE");

		}

	}

	private static void getDynamicHomeServerIP() throws IOException {
		// URL to get Local Dynamic home server IP address
		URL url = new URL("http://myexternalip.com/raw");
		// Declares "in" and allocates buffer memory for the incoming stream
		// (Code from URL).
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		// Reads the stream from variable "in" into "CodeLine".
		String CodeLine;
		String IPString = null;
		while ((CodeLine = in.readLine()) != null)
			IPString = CodeLine;

		IP = IPString;
		dynamicHomeServerIP = IPString;
		System.out.println("- Dynamic Home Server IP is: " + dynamicHomeServerIP);
	}

	private static String retrieveLastHomeServerIP() {
		try {
			// TRY DATABASE connection
			String url = "jdbc:mysql://www.ruiruivo.net:3306/Remote_DB";
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, "remote83", "1q2w3e4r");
			System.out.println("- Database Connection ESTABLISHED OK");

			// RETRIEVE last info from database
			Statement st = conn.createStatement();
			String SQLQuery = "SELECT * FROM homeserver"; // SQL QUERY to send to the DATABASE
			ResultSet lastStringFromDB = st.executeQuery(SQLQuery);
			while (lastStringFromDB.next()) {
				if (lastStringFromDB.last()) {
					String LAST_STRING_FROM_DB = lastStringFromDB.getString("homeserverip");
					System.out.println("Last: " + LAST_STRING_FROM_DB);
				}
			}

		} catch (Exception e) { // CATCH Connection error
			e.printStackTrace();
			// System.out.println(e);

		} finally { // Closes connection
			if (conn != null) {
				try {
					conn.close();
					System.out.println("Database Connection TERMINATED");
				} catch (Exception e) {/* ignore close errors */
				}
			}
		}

		return lastHomeServerIP;
	}

	// Tests Connection to server
	private static void dbConnectionTEST() {
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("- TEST - Database Connection ESTABLISHED OK");
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e);
			System.out.println("- TEST - CANNOT CONNECT TO DATABASE");
		} finally {
			if (conn != null) {
				try {
					conn.close();
					System.out.println("- TEST - Database Connection TERMINATED OK");
				} catch (Exception e) {/* ignore close errors */
				}
			}
		}

	}

}
