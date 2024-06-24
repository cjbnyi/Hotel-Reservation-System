package Hotel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;


public class View {
    public static final char CREATE_HOTEL_OPTION = 'C',
                             VIEW_HOTEL_OPTION = 'V',
                             MANAGE_HOTEL_OPTION = 'M',
                             BOOK_RESERVATION_OPTION = 'B',
                             QUIT_OPTION = 'Q';

    public static final char HIGH_LEVEL_OPTION = 'H',
                             LOW_LEVEL_OPTION = 'L';

    public static final char SELECTED_DATE_OPTION = '1',
                             SELECTED_ROOM_OPTION = '2',
                             SELECTED_RESERVATION_OPTION = '3';

    public static final int  SYSTEM_MONTH = 7,
                             SYSTEM_YEAR = 2024;
    public enum MANAGER_STATE {
        MS_OVERVIEW(0),
        MS_CHANGE_NAME(1),
        MS_ADD_ROOMS(2),
        MS_REMOVE_ROOMS(3),
        MS_UPDATE_PRICE(4),
        MS_REMOVE_RESERVATIONS(5),
        MS_REMOVE_HOTEL(6);

        private int numberID;


        private MANAGER_STATE(int numberID) {
            this.numberID = numberID;
        }

        public int getID() {
            return this.numberID;
        }

    }


    public enum SIMULATE_BOOKING {
        SB_OVERVIEW(1),
        SB_DATE_SELECTION(2),
        SB_ROOM_SELECTION(3);

        private int numberID;


        private SIMULATE_BOOKING(int numberID) {
            this.numberID = numberID;
        }

        public int getID() {
            return this.numberID;
        }

    } 

    private final Scanner scanner;

    public View() {
        this.scanner = new Scanner(System.in);
    }

    public void displayDivider() {
        System.out.println("\n========");
    }


    public void displayMessage(String message) {
        System.out.println(message);
    }


    public void displayMainActionPrompt() {
        displayDivider();
        System.out.println("Welcome to the Hotel Reservation System!\n");
        System.out.println("[C]reate Hotel");
        System.out.println("[V]iew Hotel");
        System.out.println("[M]anage Hotel");
        System.out.println("[B]ook Reservation");
    }


    public String getInputStr(String prompt) {
        String s;
        do {
            System.out.print(prompt + " ");
            s = scanner.nextLine();
            displayInvalidInputWarning(!s.isEmpty(), "Please provide a string response!");
        } while (s.isEmpty());
        return s;
    }


    public char getInputChar(String prompt) {
        String s;
        do {
            System.out.print(prompt + " ");
            s = scanner.nextLine();
            displayInvalidInputWarning(!s.isEmpty(), "Please provide a character response!");
        } while (s.isEmpty());
        return s.toUpperCase().charAt(0);
    }


    public int getInputInt(String prompt) {
        System.out.print(prompt + " ");
        do {
            displayInvalidInputWarning(scanner.hasNextInt(), "Please provide a numerical response!");
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.print(prompt + " ");
            }
        } while (!scanner.hasNextInt());

        int i = scanner.nextInt();
        scanner.nextLine();
        return i;
    }

    public double getInputDouble(String prompt) {
        System.out.print(prompt + " ");
        do {
            displayInvalidInputWarning(scanner.hasNextDouble(), "Please provide a numerical response!");
            if (!scanner.hasNextDouble()) {
                scanner.nextLine();
                System.out.print(prompt + " ");
            }
        } while (!scanner.hasNextDouble());

        double d = scanner.nextDouble();
        scanner.nextLine();
        return d;
    }

    public LocalDate getLocalDate(String prompt) {
        System.out.println(prompt + " ");
        
        System.out.println("Year: ");
        int year = scanner.nextInt();
        System.out.println("Month: ");
        int month = scanner.nextInt();    
        System.out.println("Day: ");
        int day = scanner.nextInt();
        return LocalDate.of(year, month, day);
    }
    /**
     * Prompts the user to confirm their input.
     * @return true if the user inputs 'Y'; false if the user inputs 'N'
     */
    public boolean confirmUserInput() {
        while (true){
            displayDivider();
            System.out.print("Do you want to confirm your action (Y/N)? "); // placeholder and tentative
            String userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("Y"))
                return true;
            else if (userInput.equalsIgnoreCase("N"))
                return false;
        }
    }


    public void clearConsole() {
        if (Debug.DONT_CLEAR_CONSOLE)
            for (int i = 0; i < 5; i++)
                System.out.print("\n");
        else
            System.out.print("\033\143");
    }


    public void displayCreateHotelPrompt() {
        displayDivider();
        System.out.println("[Hotel Creation]");
    }

    public void displayViewHotelPrompt() {
        displayDivider();
        System.out.println("<insert View Hotel prompt>");
    }

    public void displayManageHotelPrompt(MANAGER_STATE displayState) {
        final String[][] promptManageHotel = {
            {       // 0 : Hotel
                "# Please Choose the following Actions",
                " [a] Change the name of the Hotel",
                " [b] Add Rooms",
                " [c] Remove Rooms",
                " [d] Update the Base Price for a Room",
                " [e] Remove Reservation",
                " [f] Remove Hotel",
                "Type \"quit\" to exit the program"
            }, {
                "## Change Name of the Hotel",
                "Please provide the correct name of the hotel you want to change the name."
            }, {    // 1 : Change
                "## Add a Room(s)",
                "Please provide the correct name of the hotel and the room you want to add."
            }, {
                "## Remove a Room(s)",
                "Please provide the correct name of the hotel and the room you want to remove."
            }, {
                "## Update the Base Price",
                "Please input the price you want to change. Change should be greater than P100."
            }, {
                "## Remove Reservation"
            }, {
                "## Remove Hotel"
            }
        };

        displayDivider();
        for (String sentence : promptManageHotel[displayState.getID()]){
            System.out.println(sentence);
        }

    }

    public void displayBookReservationPrompt(SIMULATE_BOOKING displayState) {
        displayDivider();
        final String[][] promptManageHotel = {
            {
                "To book a reservation, please do the following: ",
                " 1) Enter a Valid Hotel",
                " 2) Enter a Valid Check-In Date and a Check-out Date",
                " 3) Enter a Room to Select"
            }, {
                "Please Select a Date to select from",
                "Note: Check-in Date should be earlier than the Check-out Date."
            }
        };

        displayDivider();
        for (String sentence : promptManageHotel[displayState.getID()]){
            System.out.println(sentence);
        }
    }

    public void displayProgramTerminationMessage() {
        displayDivider();
        System.out.println("Thank you for trying out our Hotel Reservation System!");
        System.out.println("Authors:");
        System.out.println("Christian Joseph C. Bunyi - @cjbnyi");
        System.out.println("Roan Cedric V. Campo - @ImaginaryLogs");
    }


    public void displayHotel(Hotel hotel) {
        displayDivider();

        System.out.println("Name: " + hotel.getName());
        System.out.println("List of rooms:");

        int i = 1;
        for (Room room : hotel.getRoomList()) {
            System.out.println(i + ".) " + room.getName());
            ++i;
        }
    }


    public void displayInvalidInputWarning(boolean isValidInput, String warning) {
        if (isValidInput) return;
        System.out.println("Invalid input! " + warning);
    }


    public void displayRoomSelection(ArrayList<Room> roomList) {
        if (roomList.isEmpty())
            System.out.println("No room exists.");
        else {
            System.out.println("List of rooms:");
            int i = 1;
            for (Room room : roomList) {
                System.out.println(i + ".) " + room.getName());
                ++i;
            }
        }
    }


    public void displayReservationSelection(ArrayList<Reservation> reservationList) {
        if (reservationList.isEmpty())
            System.out.println("\nNo reservation exists.");
        else {
            System.out.println("\nList of reservations:");
            int i = 1;
            for (Reservation reservation : reservationList) {
                System.out.println(i + ".)");
                System.out.println("Check-in date: " + reservation.getCheckInDate());
                System.out.println("Room: " + reservation.getRoom());
                ++i;
            }
        }
    }


    public void displayHotelSelection(ArrayList<Hotel> hotelList) {
        if (hotelList.isEmpty())
            System.out.println("\nNo hotel exists.");
        else {
            System.out.println("\nList of hotels:");
            int i = 1;
            for (Hotel hotel : hotelList) {
                System.out.println(i + ".) " + hotel.getName());
            }
        }

    }

    public void displayRoomList(ArrayList<Room> roomList) {
        
        if(roomList.isEmpty()){
            displayDivider();
            System.out.println("No Rooms currently exist.");
        } else {
            int i = 0;
            for (Room room : roomList){
                displayDivider();
                System.out.println(i + 1 + ".)");
                System.out.println("Room: " + room.getName());
                System.out.println("Class: " + room.getClass());
                System.out.println("Base Price: " + room.getBasePricePerNight());
                ++i;
            }
        }
    }


    public void displayHighLevelHotelInfoPrompt(boolean hasDivider, String hotelName) {
        if (hasDivider) {
            displayDivider();
            System.out.println("Selected hotel: " + hotelName + "\n");
        }
        System.out.println("[H]igh-level information");
        System.out.println("\t- Name");
        System.out.println("\t- Total number of rooms");
        System.out.println("\t- Estimated earnings for the month");
    }


    public void displayHighLevelHotelInfo(Hotel hotel, double estimatedEarnings) {
        if (hotel == null) return;
        displayDivider();
        System.out.println("Name: " + hotel.getName());
        System.out.println("Total number of rooms: " + hotel.getTotalRooms());
        System.out.println("Estimated earnings: " + estimatedEarnings);
    }


    public void displayLowLevelHotelInfoPrompt(boolean hasDivider, String hotelName) {
        if (hasDivider) {
            displayDivider();
            System.out.println("Selected hotel: " + hotelName + "\n");
        }
        System.out.println("[L]ow-level information");
        System.out.println("\t[1] Selected date hotel availability");
        System.out.println("\t\t- Total number of available rooms");
        System.out.println("\t\t- Total number of booked rooms");
        System.out.println("\t[2] Selected room information");
        System.out.println("\t\t- Name");
        System.out.println("\t\t- Price per night");
        System.out.println("\t\t- Availability across the entire month");
        System.out.println("\t[3] Selected reservation information");
        System.out.println("\t\t- Guest information");
        System.out.println("\t\t- Check-in date");
        System.out.println("\t\t- Check-out date");
        System.out.println("\t\t- Total price");
        System.out.println("\t\t- Breakdown");
    }


    public void displayHotelInfoPrompt(String hotelName) {
        displayDivider();
        System.out.println("Selected hotel: " + hotelName + "\n");
        displayHighLevelHotelInfoPrompt(false, hotelName);
        displayLowLevelHotelInfoPrompt(false, hotelName);
    }


    public void displaySelectedDateInfo(int availableRooms, int unavailableRooms) {
        System.out.println("Available rooms: " + availableRooms);
        System.out.println("Unavailable rooms: " + unavailableRooms);
    }


    public void displaySelectedRoomInfo(String roomInfo) {
        displayDivider();
        System.out.println(roomInfo);
    }


    public void displaySelectedReservationInfo(Reservation reservation) {
        if (reservation == null) return;
        displayDivider();
        System.out.println("Guest: " + reservation.getGuestName());
        System.out.println("Check-in: " + reservation.getCheckInDate());
        System.out.println("Check-out: " + reservation.getCheckOutDate());
        System.out.println("Price breakdown:" + reservation.getPriceBreakdown());
        System.out.println("Total price: " + reservation.getTotalPrice());
    }

    public void displayInvalidInputWarning() {
        displayDivider();
        System.out.println("Please provide a valid response!");
    }

    public void clearScreen(){
        if (Debug.DONT_CLEAR_CONSOLE)
            System.out.println("\033\143");
        else 
            System.out.println("\n\n\n\n\n");
    };
}