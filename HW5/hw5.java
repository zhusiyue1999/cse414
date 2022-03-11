package flightapp;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Runs queries against a back-end database
 */
public class Query {
  // DB Connection
  private Connection conn;

  // Password hashing parameter constants
  private static final int HASH_STRENGTH = 65536;
  private static final int KEY_LENGTH = 128;

  // Canned queries
  private static final String CHECK_FLIGHT_CAPACITY = "SELECT capacity FROM Flights WHERE fid = ?";
  private PreparedStatement checkFlightCapacityStatement;

  // For check dangling
  private static final String TRANCOUNT_SQL = "SELECT @@TRANCOUNT AS tran_count";
  private PreparedStatement tranCountStatement;

  // TODO: YOUR CODE HERE
  private boolean login = false;
  private String user = "";
  private int i = 0;
  private HashMap capacityOfFlights;
  
  private static final String DELETE_SQL = "DELETE * From User_info; DELETE * FROM Reservation;";
  private PreparedStatement deleteStatement;

  private static final String CHECK_USER = "SELECT * FROM User_info";
  private PreparedStatement checkUserStatement;
  
  private static final String INSERT = "INSERT INTO User_info VALUES (?, ?, ?)";
  private PreparedStatement insertStatement;

  private static final String CHECK_ITINERARY = "SELECT itineraryId FROM Itinerary";
  private PreparedStatement checkItineraryStatement;
  
  private static final String CHECK_PRICE = "SELECT username, SUM(f1_price, f2_price) FROM Flights WHERE reservationId = ?";
  private PreparedStatement checkPriceStatement;
  
  private static final String CHECK_AMOUNT = "SELECT initial FROM User_info WHERE username = ?";
  private PreparedStatement checkAmountStatement;
  
  private static final String CHECK_USER_USERNAME = "SELECT * FROM User_info WHERE username = ?";
  private PreparedStatement checkUserUsernameStatement;

  private static final String CHECK_RESERVATION_USERNAME = "SELECT * FROM Reservation WHERE username = ?";
  private PreparedStatement checkReservationUsernameStatement;

  private static final String CHECK_RESERVATION_RESERVATIONID = "SELECT * FROM Reservation WHERE reservationId = ?";
  private PreparedStatement checkReservationReservationIdStatement;

  private static final String UPDATE_AMOUNT_USERNAME = "UPDATE User_info SET initial = ? WHERE username = ?";
  private PreparedStatement updateAmountUsernameStatement;

  private static final String CANCEL_RESERVATION = "DELETE FROM Reservation WHERE reservationId = ?";
  private PreparedStatement cancelReservationStatement;
   
  private static final String RETURN_MONEY = "UPDATE User_info SET initial = ? WHERE username = ?";
  private PreparedStatement returnMoneyStatement;
  
  private static final String UPDATE_ITINERARY = "INSERT INTO Itineraries VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private PreparedStatement updateItineraryStatement;

  private static final String Search_Direct_SQL = "SELECT TOP (?) day_of_month, carrier_id, flight_num,"
        + "origin_city, dest_city, actual_time, capacity, price, fid " +
        "FROM Flights WHERE origin_city = ? AND dest_city = ? AND "
        + "day_of_month = ? ORDER BY actual_time ASC";
  private PreparedStatement searchDirectStatement;

  private static final String Search_Indirect_SQL = "SELECT TOP (?) day_of_month, F1.flight_num, F2.flight_num,"
        + "F1.actual_time, F2.actual_time, F1.capacity, F2.capacity,"
        + "F1.price, F2.price, F1.origin_city, F2.origin_city, F1.fid, F2.fid,"
        + "F1.dest_city, F2.dest_city FROM Flights as F1, Flights as F2 "
        + "WHERE F1.day_of_month = ? AND F2.day_of_month = ? AND "
        + "F1.origin_city = ? AND F1.dest_city = F2.dest_city AND "
        + "F2.dest_city = ? ORDER BY (F1.actual_time + F2.actual_time) as time ASC";
  private PreparedStatement searchIndirectStatement;
  
  private static final String CHECK_NEWEST_RESERVATIONID = "SELECT max(reservationId) AS num FROM Reservation";
  private PreparedStatement checkNewestReservationIdStatement;
  
  private static final String UPDATE_RESERVATION = "INSERT INTO Reservation VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private PreparedStatement updateReservationStatement;

  private static final String CHECK_ITINERARY = "SELECT * FROM Itineraries Where iid = ?";
  private PreparedStatement checkItineraryStatement;
  
  private static final String CHECK_RESERVATION = "SELECT * FROM Reservation";
  private PreparedStatement checkReservationStatement;


  
  public Query() throws SQLException, IOException {
    this(null, null, null, null);
  }

  protected Query(String serverURL, String dbName, String adminName, String password)
      throws SQLException, IOException {
    conn = serverURL == null ? openConnectionFromDbConn()
        : openConnectionFromCredential(serverURL, dbName, adminName, password);

    prepareStatements();
  }

  /**
   * Return a connecion by using dbconn.properties file
   *
   * @throws SQLException
   * @throws IOException
   */
  public static Connection openConnectionFromDbConn() throws SQLException, IOException {
    // Connect to the database with the provided connection configuration
    Properties configProps = new Properties();
    configProps.load(new FileInputStream("dbconn.properties"));
    String serverURL = configProps.getProperty("hw5.server_url");
    String dbName = configProps.getProperty("hw5.database_name");
    String adminName = configProps.getProperty("hw5.username");
    String password = configProps.getProperty("hw5.password");
    return openConnectionFromCredential(serverURL, dbName, adminName, password);
  }

  /**
   * Return a connecion by using the provided parameter.
   *
   * @param serverURL example: example.database.widows.net
   * @param dbName    database name
   * @param adminName username to login server
   * @param password  password to login server
   *
   * @throws SQLException
   */
  protected static Connection openConnectionFromCredential(String serverURL, String dbName,
      String adminName, String password) throws SQLException {
    String connectionUrl =
        String.format("jdbc:sqlserver://%s:1433;databaseName=%s;user=%s;password=%s", serverURL,
            dbName, adminName, password);
    Connection conn = DriverManager.getConnection(connectionUrl);

    // By default, automatically commit after each statement
    conn.setAutoCommit(true);

    // By default, set the transaction isolation level to serializable
    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

    return conn;
  }

  /**
   * Get underlying connection
   */
  public Connection getConnection() {
    return conn;
  }

  /**
   * Closes the application-to-database connection
   */
  public void closeConnection() throws SQLException {
    conn.close();
  }

  /**
   * Clear the data in any custom tables created.
   * 
   * WARNING! Do not drop any tables and do not clear the flights table.
   */
  public void clearTables() {
    try {
      // TODO: YOUR CODE HERE
      deleteStatement.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * prepare all the SQL statements in this method.
   */
  private void prepareStatements() throws SQLException {
    checkFlightCapacityStatement = conn.prepareStatement(CHECK_FLIGHT_CAPACITY);
    tranCountStatement = conn.prepareStatement(TRANCOUNT_SQL);
    // TODO: YOUR CODE HERE
    deleteStatement = conn.prepareStatement(DELETE_SQL);
    checkUserStatement = conn.prepareStatement(CHECK_USER);
    checkReservationStatement = conn.prepareStatement(CHECK_RESERVATION);
    checkItineraryStatement = conn.prepareStatement(CHECK_ITINERARY);
  }

  /**
   * Takes a user's username and password and attempts to log the user in.
   *
   * @param username user's username
   * @param password user's password
   *
   * @return If someone has already logged in, then return "User already logged in\n" For all other
   *         errors, return "Login failed\n". Otherwise, return "Logged in as [username]\n".
   */
  public String transaction_login(String username, String password) {
    try {
      // TODO: YOUR CODE HERE
      // Generate a random cryptographic salt
      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[16];
      random.nextBytes(salt);
      
      // Specify the hash parameters
      KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_STRENGTH, KEY_LENGTH);
      
      // Generate the hash
      SecretKeyFactory factory = null;
      byte[] hash = null;
      try {
        factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        hash = factory.generateSecret(spec).getEncoded();
      } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
        throw new IllegalStateException();
      }

      ResultSet rs = checkUserStatement.executeQuery();
      if(!login) {
         return "User already logged in\n";
      } else {
         while(rs.next()) {
            if(rs.getString("username") == username && rs.getString("pass") == hash) {
               login = true;
               user = username;
               return "Logged in as "+ username +" \n";

            }
         }
         return "Login failed\n";
      }
    } finally {
      checkDanglingTransaction();
    }
  }

  /**
   * Implement the create user function.
   *
   * @param username   new user's username. User names are unique the system.
   * @param password   new user's password.
   * @param initAmount initial amount to deposit into the user's account, should be >= 0 (failure
   *                   otherwise).
   *
   * @return either "Created user {@code username}\n" or "Failed to create user\n" if failed.
   */
  public String transaction_createCustomer(String username, String password, int initAmount) {
    try {
      // TODO: YOUR CODE HERE
      // Generate a random cryptographic salt
      SecureRandom random = new SecureRandom();
      byte[] salt = new byte[16];
      random.nextBytes(salt);
      
      // Specify the hash parameters
      KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_STRENGTH, KEY_LENGTH);
      
      // Generate the hash
      SecretKeyFactory factory = null;
      byte[] hash = null;
      try {
        factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        hash = factory.generateSecret(spec).getEncoded();
      } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
        throw new IllegalStateException();
      }

      
      ResultSet rs = checkUsernamePassStatement.executeQuery();
      if(initAmount < 0) {
         return "Failed to create user\n";
      }
      while(rs.next()) {
         if(rs.getString("username") == username) {
            return "Failed to create user\n";
         }
      }
      insertStatement.clearParameters();
      insertStatement.setString(1, username);
      insertStatement.setString(2, hash);
      insertStatement.setString(3, initAmount);
      ResultSet rs1 = insertStatement.executeQuery();

      return "Created user " + username + " \n";
    } finally {
      checkDanglingTransaction();
    }
  }

  /**
   * Implement the search function.
   *
   * Searches for flights from the given origin city to the given destination city, on the given day
   * of the month. If {@code directFlight} is true, it only searches for direct flights, otherwise
   * is searches for direct flights and flights with two "hops." Only searches for up to the number
   * of itineraries given by {@code numberOfItineraries}.
   *
   * The results are sorted based on total flight time.
   *
   * @param originCity
   * @param destinationCity
   * @param directFlight        if true, then only search for direct flights, otherwise include
   *                            indirect flights as well
   * @param dayOfMonth
   * @param numberOfItineraries number of itineraries to return
   *
   * @return If no itineraries were found, return "No flights match your selection\n". If an error
   *         occurs, then return "Failed to search\n".
   *
   *         Otherwise, the sorted itineraries printed in the following format:
   *
   *         Itinerary [itinerary number]: [number of flights] flight(s), [total flight time]
   *         minutes\n [first flight in itinerary]\n ... [last flight in itinerary]\n
   *
   *         Each flight should be printed using the same format as in the {@code Flight} class.
   *         Itinerary numbers in each search should always start from 0 and increase by 1.
   *
   * @see Flight#toString()
   */
   public String transaction_search(String originCity, String destinationCity, boolean directFlight,
                                 int dayOfMonth, int numberOfItineraries) {
    try {

        StringBuffer sb = new StringBuffer();
        try {
            
            searchDirectStatement.setInt(1, numberOfItineraries);
            searchDirectStatement.setString(2, originCity);
            searchDirectStatement.setString(3, destinationCity);
            searchDirectStatement.setInt(4, dayOfMonth);
            ResultSet directResults = searchDirectStatement.executeQuery();

            List<info> directItineraries = new ArrayList();
            int count = 0;
            while (directResults.next() && count < numberOfItineraries) {
                info curr = new info(count, dayOfMonth, directResults.getInt("fid"),
                        directResults.getString("carrierID"),
                        directResults.getString("flightNum"), originCity,
                        destinationCity, directResults.getInt("F1.actual_time"),
                        directResults.getInt("capacity"),
                        directResults.getInt("price"), 0, null,
                        null, null, null, 0, 0,
                        0, false);
                directItineraries.add(curr);
                count++;
            }
            directResults.close();

            List<info> totalItineraries = new ArrayList();
            totalItineraries.addAll(directItineraries);

            if (!directFlight) {
                searchIndirectStatement.setInt(1, numberOfItineraries);
                searchIndirectStatement.setInt(2, dayOfMonth);
                searchIndirectStatement.setInt(3, dayOfMonth);
                searchIndirectStatement.setString(4, originCity);
                searchIndirectStatement.setString(5, destinationCity);
                ResultSet indirectResults = searchIndirectStatement.executeQuery();

                int numIndirectItineraries = numberOfItineraries - count;

                while (indirectResults.next() && numIndirectItineraries > 0) {
                    info curr = new info(0, dayOfMonth, indirectResults.getInt("F1.fid"),
                            indirectResults.getString("F1.carrierID"),
                            indirectResults.getString("F1.flightNum"), originCity,
                            indirectResults.getString("F1.dest_city"),
                            indirectResults.getInt("F1.actual_time"),
                            indirectResults.getInt("F1.capacity"),
                            indirectResults.getInt("F1.price"),
                            indirectResults.getInt("F2.fid"),
                            indirectResults.getString("F2.carrierID"),
                            indirectResults.getString("F2.flightNum"),
                            indirectResults.getString("F2.origin_city"), destinationCity,
                            indirectResults.getInt("F2.actual_time"),
                            indirectResults.getInt("F2.capacity"),
                            indirectResults.getInt("F2.price"), true);
                    totalItineraries.add(curr);
                    numIndirectItineraries--;
                }
                indirectResults.close();

                Collections.sort(totalItineraries, new Comparator<info>() {
                    @Override
                    public int compare(info u1, info u2) {
                        return u1.getTotalTime() - u2.getTotalTime();
                    }
                });
            }

            int iid = 0;
            for (info itinerary : totalItineraries) {
                itinerary.setIid(iid);
                if (!itinerary.getf2_exists()) {
                    sb.append("Itinerary " + iid + " 1 flight(s), " + itinerary.getTotalTime() +
                            " minutes \nID: " + itinerary.getf1_fid() + " Day: " +
                            itinerary.getDayOfMonth() + " Carrier: " + itinerary.getf1_carrierId() +
                            " Number: " + itinerary.getf1_flightNum() + " Origin: " + itinerary.getf1_originCity() +
                            " Dest: " + itinerary.getf1_destCity() + " Duration: " + itinerary.getf1_time() +
                            " Capacity: " + itinerary.getf1_capacity() + " Price: " + itinerary.getf1_price()); //fix capacity
                } else {
                    sb.append("Itinerary " + iid + " 1 flight(s), " + itinerary.getTotalTime() +
                            " minutes \nID: " + itinerary.getf1_fid() + " Day: " +
                            itinerary.getDayOfMonth() + " Carrier: " + itinerary.getf1_carrierId() +
                            " Number: " + itinerary.getf1_flightNum() + " Origin: " + itinerary.getf1_originCity() +
                            " Dest: " + itinerary.getf1_destCity() + " Duration: " + itinerary.getf1_time() +
                            " Capacity: " + itinerary.getf1_capacity() + " Price: " + itinerary.getf1_price() +
                            "\nID: " + itinerary.getf2_fid() + " Day: " + itinerary.getDayOfMonth() + " Carrier: " + //fix capacity
                            itinerary.getf2_carrierId() + " Number: " + itinerary.getf2_flightNum() + "Origin: " +
                            itinerary.getf2_time() + " Capacity: " + itinerary.getf2_capacity() + " Price: " + //fix capacity
                            itinerary.getf2_price());
                }
                iid++;
            }
        } catch(Exception e) {

            e.printStackTrace();
        }
            return sb.toString();
    } finally {
        checkDanglingTransaction();
    }
}


/**
   * Implements the book itinerary function.
   *
   * @param itineraryId ID of the itinerary to book. This must be one that is returned by search in
   *                    the current session.
   *
   * @return If the user is not logged in, then return "Cannot book reservations, not logged in\n".
   *         If the user is trying to book an itinerary with an invalid ID or without having done a
   *         search, then return "No such itinerary {@code itineraryId}\n". If the user already has
   *         a reservation on the same day as the one that they are trying to book now, then return
   *         "You cannot book two flights in the same day\n". For all other errors, return "Booking
   *         failed\n".
   *
   *         And if booking succeeded, return "Booked flight(s), reservation ID: [reservationId]\n"
   *         where reservationId is a unique number in the reservation system that starts from 1 and
   *         increments by 1 each time a successful reservation is made by any user in the system.
   */
  public String transaction_book(int itineraryId) {
    try {
      // TODO: YOUR CODE HERE
      ResultSet rs = checkNewestReservationIdStatement.executeQuery();
      
      if(!login) {
         return "Cannot book reservations, not logged in\n";
      } else if(rs.next()) {
         return "You cannot book two flights in the same day\n";
      } else if(itineraryId == 0) {
         return "No such itinerary {@code itineraryId}\n";
      }
         checkItineraryStatement.clearParameters();
         checkItineraryStatement.setInt(1, itineraryId);
         ResultSet rs1 = checkItineraryStatement.executeQuery();
         checkAmountStatement.setInt(1, user);
         ResultSet rs3 = checkAmountStatement.executeQuery();

      if(rs1.getInt("f1_capacity") > 0 && rs3.getInt("initial") >= rs1.getInt("f1_price")) {
         if(!rs1.getBoolean("f2_exists") || 
            (rs1.getBoolean("f2_exists") && rs2.getInt("f2_capacity") > 0 
            && rs3.getInt("initial") >= rs1.getInt("f1_price") + rs1.getInt("f2_price"))) {
            ResultSet rs2 = checkNewestReservationIdStatement.executeQuery();
            updateReservationStatement.clearParameters();
            updateReservationStatement.setString(1, user); //username
            updateReservationStatement.setInt(2, rs2.getInt("reservationId") + 1); //reservationId
            updateReservationStatement.setBoolean(3, false); //paid
            updateReservationStatement.setInt(4, rs1.getInt("f1_fid")); //f1_fid
            updateReservationStatement.setInt(5, rs1.getInt("f1_day_of_month")); //f1_day_of_month
            updateReservationStatement.setString(6, rs1.getString("f1_carrier_id")); //f1_carrier_id
            updateReservationStatement.setInt(7, rs1.getInt("f1_flight_num")); //f1_flight_num
            updateReservationStatement.setString(8, rs1.getString("f1_origin_city"));//f1_origin_city
            updateReservationStatement.setString(9, rs1.getString("f1_dest_city")); //f1_dest_city
            updateReservationStatement.setInt(10, rs1.getInt("f1_actual_time")); //f1_actual_time
            updateReservationStatement.setInt(11, rs1.getInt("f1_capacity")); //f1_capacity
            updateReservationStatement.setInt(12, rs1.getInt("f1_price")); //f1_price
            if(rs1.getBoolean("f2_exists")) {
               updateReservationStatement.setInt(13, rs1.getInt("f2_fid")); //f2_fid
               updateReservationStatement.setInt(14, rs1.getInt("f2_day_of_month")); //f2_day_of_month
               updateReservationStatement.setString(15, rs1.getString("f2_carrier_id")); //f2_carrier_id
               updateReservationStatement.setInt(16, rs1.getInt("f2_flight_num")); //f2_flight_num
               updateReservationStatement.setString(17, rs1.getString("f2_origin_city")); //f2_origin_city
               updateReservationStatement.setString(18, rs1.getString("f2_dest_city")); //f2_dest_city
               updateReservationStatement.setInt(19, rs1.getInt("f2_actual_time")); //f2_actual_time
               updateReservationStatement.setInt(20, rs1.getInt("f2_capacity")); //f2_capacity
               updateReservationStatement.setInt(21, rs1.getInt("f2_price")); //f2_price
               updateReservationStatement.setBoolean(22, true); //f2_exists
               updateReservationStatement.execute();
            } else {
               updateReservationStatement.setInt(13, null); //f2_fid
               updateReservationStatement.setInt(14, null); //f2_day_of_month
               updateReservationStatement.setString(15, null); //f2_carrier_id
               updateReservationStatement.setInt(16, null); //f2_flight_num
               updateReservationStatement.setString(17, null); //f2_origin_city
               updateReservationStatement.setString(18, null); //f2_dest_city
               updateReservationStatement.setInt(19, null); //f2_actual_time
               updateReservationStatement.setInt(20, null); //f2_capacity
               updateReservationStatement.setInt(21, null); //f2_price
               updateReservationStatement.setBoolean(22, false); //f2_exists
               updateReservationStatement.execute();
            }
         }
         checkReservationStatement.
         ResultSet rs4 = checkReservationStatement.executeQuery();
         while(rs4.next()) {
            int current_fid = rs4.getInt("f1_fid");
            if(capacityOfFlights.containsKey(current_fid)) {
               capacityOfFlights.replace(current_fid, capacityOfFlights.get(current_fid) - 1);
            } else {
               capacityOfFlights.put(current_fid, rs4.getInt("f1_capacity"));
            }
            if(rs4.getBoolean("f2_exists")) {
               int current_fid_2 = rs4.getInt("f1_fid");
               if(capacityOfFlights.containsKey(current_fid_2)) {
                  capacityOfFlights.replace(current_fid, capacityOfFlights.get(current_fid_2) - 1);
               } else {
                  capacityOfFlights.put(current_fid_2, rs4.getInt("f1_capacity"));
               }
            }
         }
         return "Booked flight(s), reservation ID: " + (rs2.getInt("reservationId") + 1) + "\n";
      }
      return "Booking failed\n";
    } finally {
      checkDanglingTransaction();
    }
  }

  /**
   * Implements the pay function.
   *
   * @param reservationId the reservation to pay for.
   *
   * @return If no user has logged in, then return "Cannot pay, not logged in\n" If the reservation
   *         is not found / not under the logged in user's name, then return "Cannot find unpaid
   *         reservation [reservationId] under user: [username]\n" If the user does not have enough
   *         money in their account, then return "User has only [balance] in account but itinerary
   *         costs [cost]\n" For all other errors, return "Failed to pay for reservation
   *         [reservationId]\n"
   *
   *         If successful, return "Paid reservation: [reservationId] remaining balance:
   *         [balance]\n" where [balance] is the remaining balance in the user's account.
   */
  public String transaction_pay(int reservationId) {
    try {
      // TODO: YOUR CODE HERE
      checkReservationReservationIdStatement.clearParameters();
      checkReservationReservationIdStatement.setString(1, reservationId);
      ResultSet rs1 = checkReservationReservationIdStatement.executeQuery();
      
      checkUserUsernameStatement.clearParameters();
      checkUserUsernameStatement.setString(1, user);
      ResultSet rs2 = checkUserUsernameStatement.executeQuery();
      Int total = 0;

      if(!login) {
         return "Cannot pay, not logged in\n";
      } else if(rs1.getString("username") != user) {
         return "Cannot find unpaid reservation "+ reservationId + " under user: "+ username +"\n";
      } else {
         if(rs.getBoolean("f2_exists")) {
            total = rs.getInt("f1_price") + rs.getInt("f2_price");
            if(total > rs2.getInt("initial")) {
               return "User has only " + rs2.getInt("initial") + " in account but itinerary costs " + rs.getInt("f1_price") + rs.getInt("f2_price") + "\n";
            }
         } else {
            total = rs.getInt("f1_price");
            if(total > rs2.getInt("initial")) {
               return "User has only " + rs2.getInt("initial") + " in account but itinerary costs " + rs.getInt("f1_price") + "\n";
            }
         } 
      }
      
      updateAmountUsernameStatement.clearParameters();
      updateAmountUsernameStatement.setString(1, rs2.getInt("initial") - total);
      updateAmountUsernameStatement.setString(2, user);
      updateAmountUsernameStatement.execute();
      return "Paid reservation: "+ reservationId +" remaining balance: "+ rs2.getInt("initial") - total +"\n";
      
    } finally {
      checkDanglingTransaction();
    }
    return "Failed to pay for reservation " + reservationId + "\n";
  }

  /**
   * Implements the reservations function.
   *
   * @return If no user has logged in, then return "Cannot view reservations, not logged in\n" If
   *         the user has no reservations, then return "No reservations found\n" For all other
   *         errors, return "Failed to retrieve reservations\n"
   *
   *         Otherwise return the reservations in the following format:
   *
   *         Reservation [reservation ID] paid: [true or false]:\n [flight 1 under the
   *         reservation]\n [flight 2 under the reservation]\n Reservation [reservation ID] paid:
   *         [true or false]:\n [flight 1 under the reservation]\n [flight 2 under the
   *         reservation]\n ...
   *
   *         Each flight should be printed using the same format as in the {@code Flight} class.
   *
   * @see Flight#toString()
   */
  public String transaction_reservations() {
    try {
      // TODO: YOUR CODE HERE
      checkReservationUsernameStatement.clearParameters();
      checkReservationUsernameStatement.setString(1, user);
      ResultSet rs = checkReservationUsernameStatement.executeQuery();
      StringBuffer sb = new StringBuffer();
      
      if(!login) {
         return "Cannot view reservations, not logged in\n";
      } 
      while(rs.next()) {
         int rid = rs.getInt("rid");
         String paid = "false"; 
         if (rs.getBoolean("paid")) {
            paid = "true";
         }
         Flight one = new Flight(rs.getInt("f1_id"), rs.getInt("f1_dayOfMonth"),
                rs.getString("f1_carrierID"), rs.getString("f1_flightNum"),
                rs.getString("f1_originCity"), rs.getString("f1_destCity"),
                rs.getInt("f1_time"), rs.getInt("f1_capacity"),
                rs.getInt("f1_price"));
         Flight two = new Flight(rs.getInt("f2_id"), rs.getInt("f2_dayOfMonth"),
                rs.getString("f2_carrierID"), rs.getString("f2_flightNum"),
                rs.getString("f2_originCity"), rs.getString("f2_destCity"),
                rs.getInt("f2_time"), rs.getInt("f2_capacity"),
                rs.getInt("f2_price"));
         sb.append("Reservation "+ rid +" paid: "+ paid +":\n");
         sb.append(one + "\n");
         if(rs.getBoolean(f2_exists)) {
            sb.append(two + "\n");
         }
         String result = sb.toString();
         if(result.isEmpty()) {
            return "No reservations found\n";
         } else {
            return result;
         }
      }
      return "Failed to retrieve reservations\n";
      } finally {
      checkDanglingTransaction();
      }

    }
  

  /**
   * Implements the cancel operation.
   *
   * @param reservationId the reservation ID to cancel
   *
   * @return If no user has logged in, then return "Cannot cancel reservations, not logged in\n" For
   *         all other errors, return "Failed to cancel reservation [reservationId]\n"
   *
   *         If successful, return "Canceled reservation [reservationId]\n"
   *
   *         Even though a reservation has been canceled, its ID should not be reused by the system.
   */
  public String transaction_cancel(int reservationId) {
    try {
      // TODO: YOUR CODE HERE
      if(!login) {
         return "Cannot cancel reservations, not logged in\n";
      }
      cancelReservationStatement.clearParameters();
      cancelReservationStatement.setString(1, reservationId);
      cancelReservationStatement.execute();
      
      checkReservationReservationIdStatement.clearParameters();
      checkReservationReservationIdStatement.
      ResultSet rs1 = checkReservationReservationIdStatement.executeQuery();
      
      checkUserUsernameStatement.clearParameters();
      checkUserUsernameStatement.setString(1, user);
      ResultSet rs2 = returnMoneyStatement.executeQuery();
      
      int total = 0;
      int fid1 = "";
      int fid2 = "";
      if(rs1.getBoolean("f2_exists")) {
         total = rs1.getInt("f1_price") + rs1.getInt("f2_price");
         fid1 = rs1.getInt("f1_fid");
         fid2 = rs1.getInt("f2_fid");
         capacityOfFlights.replace(fid1, capacityOfFlights.get(fid1) + 1);
         capacityOfFlights.replace(fid2, capacityOfFlights.get(fid2) + 1);
      } else {
         total = rs1.getInt("f1_price");
         fid1 = rs1.getInt("f1_fid");
         capacityOfFlights.replace(fid1, capacityOfFlights.get(fid1) + 1);
      }  
      
      returnMoneyStatement.clearParameters();
      returnMoneyStatement.setInt(1, total + rs2.getInt("initial"));
      returnMoneyStatement.setString(2, user);
      returnMoneyStatement.execute();
      
         
      return "Canceled reservation " + reservationId +"\n";
    } finally {
      checkDanglingTransaction();
    }
    return "Failed to cancel reservation " + reservationId + "\n";

  }

  /**
   * Example utility function that uses prepared statements
   */
  private int checkFlightCapacity(int fid) throws SQLException {
    checkFlightCapacityStatement.clearParameters();
    checkFlightCapacityStatement.setInt(1, fid);
    ResultSet results = checkFlightCapacityStatement.executeQuery();
    results.next();
    int capacity = results.getInt("capacity");
    results.close();

    return capacity;
  }

  /**
   * Throw IllegalStateException if transaction not completely complete, rollback.
   * 
   */
  private void checkDanglingTransaction() {
    try {
      try (ResultSet rs = tranCountStatement.executeQuery()) {
        rs.next();
        int count = rs.getInt("tran_count");
        if (count > 0) {
          throw new IllegalStateException(
              "Transaction not fully commit/rollback. Number of transaction in process: " + count);
        }
      } finally {
        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Database error", e);
    }
  }
  
  private static boolean isDeadLock(SQLException ex) {
    return ex.getErrorCode() == 1205;
  }

  /**
   * A class to store flight information.
   */
  class Flight {
    public int fid;
    public int dayOfMonth;
    public String carrierId;
    public String flightNum;
    public String originCity;
    public String destCity;
    public int time;
    public int capacity;
    public int price;

    @Override
    public String toString() {
      return "ID: " + fid + " Day: " + dayOfMonth + " Carrier: " + carrierId + " Number: "
          + flightNum + " Origin: " + originCity + " Dest: " + destCity + " Duration: " + time
          + " Capacity: " + capacity + " Price: " + price;
    }
  }
  
  class info implements comparator<info> {
    public int iid;
    public int f1_fid;
    public int f1_dayOfMonth;
    public String f1_carrierId;
    public String f1_flightNum;
    public String f1_originCity;
    public String f1_destCity;
    public int f1_time;
    public int f1_capacity;
    public int f1_price;
    public int f2_fid;
    public int f2_dayOfMonth;
    public String f2_carrierId;
    public String f2_flightNum;
    public String f2_originCity;
    public String f2_destCity;
    public int f2_time;
    public int f2_capacity;
    public int f2_price;  
    public boolean f2_exists;
    
    public void addData(int iid, int f1_fid, int f1_dayOfMonth, String f1_carrierId, String f1_flightNum,
                        String f1_originCity, String f1_destCity, int f1_time, int f1_capacity,
                        int f1_price, int f2_fid int f2_dayOfMonth, String f2_carrierId,
                        String f2_flightNum, String f2_originCity, String f2_destCity, 
                        int f2_time, int f2_capacity, int f2_price, int total_time, boolean f2_exists) {
    this.iid = iid;
    this.f1_fid = f1_fid;
    this.f1_dayOfMonth = f1_dayOfMonth;
    this.f1_carrierId = f1_carrierId;
    this.f1_flightNum = f1_flightNum;
    this.f1_originCity = f1_originCity;
    this.f1_destCity = f1_destCity;
    this.f1_time = f1_time;
    this.f1_capacity = f1_capacity;
    this.f1_price = f1_price;
    this.f2_fid = f2_fid;
    this.f2_dayOfMonth = f2_dayOfMonth;
    this.f2_carrierId = f2_carrierId;
    this.f2_flightNum = f2_flightNum;
    this.f2_originCity = f2_originCity;
    this.f2_destCity = f2_destCity;
    this.f2_time = f2_time;
    this.f2_capacity = f2_capacity;
    this.f2_price = f2_price;
    this.total_time = total_time;
    this.f2_exists = f2_exists;
    }
    
    public int getiid() {
      return this.iid;
    }
    
    public int getf1_fid() {
      return this.f1_fid;
    }
    
    public int getf1_dayOfMonth() {
      return this.f1_dayOfMonth;
    }
    
    public String getf1_carrierId() {
      return this.f1_carrierId;
    }
    
    public String getf1_flightNum() {
      return this.f1_flightNum;
    }
    
    public String getf1_originCity() {
      return this.f1_originCity;
    }
    
    public String getf1_destCity() {
      return this.f1_destCity;
    }
    
    public int getf1_time() {
      return this.f1_time;
    }
    
    public int getf1_capacity() {
      return this.f1_capacity;
    }
    
    public int getf1_price() {
      return this.f1_price;
    }
    
    public int getf2_fid() {
      return this.f2_fid;
    }
    
    public int getf2_dayOfMonth() {
      return this.f2_dayOfMonth;
    }
    
    public String getf2_carrierId() {
      return this.f2_carrierId;
    }
    
    public String getf2_flightNum() {
      return this.f2_flightNum;
    }
    
    public String getf2_originCity() {
      return this.f2_originCity;
    }
    
    public String getf2_destCity() {
      return this.f2_destCity;
    }
    
    public int getf2_time() {
      return this.f2_time;
    }
    
    public int getf2_capacity() {
      return this.f2_capacity;
    }
    
    public int getf2_price() {
      return this.f2_price;
    }  
    
    public boolean getf2_exists() {
      return this.f2_exists;
    }
    
    public int gettotal_time() {
      return this.total_time;
    }
    
    

  }
  
  Arraylist<info> info_list = new Arraylist<info>();
  
}




