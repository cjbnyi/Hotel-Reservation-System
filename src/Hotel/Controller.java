package Hotel;

import static Hotel.View.MANAGER_STATE.MS_ADD_ROOMS;
import static Hotel.View.MANAGER_STATE.MS_CHANGE_NAME;
import static Hotel.View.MANAGER_STATE.MS_CHOSE_HOTEL;
import static Hotel.View.MANAGER_STATE.MS_OVERVIEW;
import static Hotel.View.MANAGER_STATE.MS_REMOVE_HOTEL;
import static Hotel.View.MANAGER_STATE.MS_REMOVE_RESERVATIONS;
import static Hotel.View.MANAGER_STATE.MS_REMOVE_ROOMS;
import static Hotel.View.MANAGER_STATE.MS_UPDATE_PRICE;
import static Hotel.View.SIMULATE_BOOKING.SB_DATE_SELECTION;
import static Hotel.View.SIMULATE_BOOKING.SB_GUEST_SELECTION;
import static Hotel.View.SIMULATE_BOOKING.SB_HOTEL_SELECTION;
import static Hotel.View.SIMULATE_BOOKING.SB_OVERVIEW;
import static Hotel.View.SIMULATE_BOOKING.SB_RESERVATION_CONFIRMATION;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import Hotel.HotelGUI.PANEL_NAME;
import Hotel.View.MANAGER_STATE;


/**
 * The {@code Controller} class acts as the intermediary between the {@code Model} and {@code View} in the
 * Model-View-Controller (MVC) design pattern. It handles user input and interacts
 * with the model and view to perform the necessary operations.
 */
public class Controller implements ActionListener, DocumentListener, ListSelectionListener {
    // System date constants
    public static final int  SYSTEM_MONTH = 7,
                             SYSTEM_YEAR = 2024;
    private Model model;
    private View view;
    private HotelGUI gui;
    private boolean hasConfirmed;
    private boolean hasAgreed;
    private Boolean[] hasSelectedHotel;
    private String actionToConfirm;
    private Integer[] selectedHotels;
    /**
     * Constructs a Controller with the specified Model and View.
     *
     * @param model the model to be used by the controller
     * @param view the view to be used by the controller
     */
    public Controller(Model model, View view, HotelGUI view2) {
        this.model = model;
        this.view = view;
        this.gui = view2;
        this.actionToConfirm = "";
        this.hasConfirmed = false;
        this.hasAgreed = false;
        Integer selectedHotel[] = {0, 0, 0, 0};
        this.selectedHotels = selectedHotel;
        Boolean hasSelectedHotel[] = {false, false, false, false};
        this.hasSelectedHotel = hasSelectedHotel;
        view2.setActionListener(this);
        view2.setDocumentListener(this);
        view2.setListSelectionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String event = e.getActionCommand();
        
        // TODO: Remove once done debugging

        System.out.println("\"" + event + "\"");
        controllerActions(event);
        
    }
    
    private void controllerActions(String event){
        
        // General
        switch (event){
            case "Yes":
                this.hasAgreed = true;
                controllerActions(this.actionToConfirm);
                break;
            case "No":
                this.hasAgreed = false;
                controllerActions(this.actionToConfirm);
                break;
        }

        // Home Panel
        switch (event){
            case "Create Hotel":
                gui.selectJtabbedPanelView(1);
                break;
            case "View Hotel":
                gui.selectJtabbedPanelView(2);
                break;
            case "Manage Hotel":
                gui.selectJtabbedPanelView(3);
                break;
            case "Book Hotel":
                gui.selectJtabbedPanelView(4);
                break;
            case "Quit Manager":
                System.exit(0);
                break;
        }

        // Hotel Selection Panel and Create Panel
        switch (event){
            case "<":
                getPrevHotel();
                break;
            case ">":
                getNextHotel();
                break;
            case "Select Hotel":
                confirmSelectedHotel();
                break;
            case "Create Hotel Instance":
                guiCreateHotel();
                break;
        }

        // View Panel
        switch(event){
            case "View High level":
                guiViewHighHotel();
                break;
            case "View Date":
                guiLowLevelDate();
                break;
            case "View Room":
                guiLowLevelRoom();
                break;
            case "View Reservation":
                guiLowLevelReservation();
                break;
        }

        // Manage Panel
        switch(event) {
            case "Change Name":
                guiChangeName();
                break;
            case "Add Rooms":
                guiAddRooms();
                break;
            case "Remove Rooms":
                guiRemoveRooms();
                break;
            case "Update Price":
                guiUpdatePrice();
                break;
            case "Remove Reservation":
                guiRemoveReservation();
                break;
            case "Remove Hotel":
                guiRemoveHotel();
                break;
        }

        // Book Panel
        switch (event){
            case "Book Reservation":
                guiBookReservation();
            break;  
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e){
        String output;
        try {
            output = e.getDocument().getText(0, e.getDocument().getLength());
            System.out.println("Insert: " + output);
        } catch (BadLocationException Error){
            System.out.println("Error");
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e){
        String output;
        try {
            output = e.getDocument().getText(0, e.getDocument().getLength());
            System.out.println("Changed: " + output);
        } catch (BadLocationException Error){
            System.out.println("Error");
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e){
        String output;
        try {
            output = e.getDocument().getText(0, e.getDocument().getLength());
            System.out.println("Removed: " + output);
        } catch (BadLocationException Error){
            System.out.println("Error");
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e){
        JList lsm = (JList) e.getSource();
        String output = "";
        
        
        if (lsm.isSelectionEmpty()) {
            System.out.println("None");
        } else {
            // Find out which indexes are selected.
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                    System.out.println(i);
                }
            }
        }
    }

    private void guiViewHighHotel(){
        PanelViewHotel viewPanel = this.gui.getPanelViewHotel();
        Hotel hotel = getHotelPanelSelectedHotel(viewPanel);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }
        double estimatedEarnings = model.getHotelEstimatedEarnings(hotel);
  
        String output = "Hotel name: " + hotel.getName() + "\nTotal number of rooms: " + hotel.getTotalRooms() + "\nEstimated earnings: " + estimatedEarnings;
        viewPanel.setContentInfo(output);
    }

    private void guiLowLevelDate(){
        PanelViewHotel viewPanel = this.gui.getPanelViewHotel();
        Hotel hotel = getHotelPanelSelectedHotel(viewPanel);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }

        int daySelection = viewPanel.getjLDateSelected().getSelectedIndex();
        if (daySelection == -1){
            viewPanel.setContentInfo("Select a date.");
            return;
        }
        LocalDate date = LocalDate.of(2024, View.SYSTEM_MONTH, viewPanel.getjLDateSelected().getSelectedValue());
        
        int availableRooms = model.getNumOfAvailableRoomsByDate(hotel, date);
        String output = "Hotel: " + hotel.getName() + "\nAvailable Rooms: " + availableRooms  + "\nUnvailable Rooms: " + (hotel.getTotalRooms() - availableRooms);
        viewPanel.setContentInfo(output);
    }

    /**
     * Displays the list of rooms for selection.
     * @param roomList the list of rooms to display
     */
    private void guiDisplayRoomSelection(HotelPanel hotelPanel, String hotelName, ArrayList<Room> roomList) {
        if (roomList.isEmpty()){
            hotelPanel.addContentInfo("\nNo room currently exists.");
            return;
        }
        String output = "Selected hotel: " + hotelName + "\nList of rooms:\n";
        int i = 1;
        for (Room room : roomList) {
            if (i % 5 == 1)
                output += "\n";
            output +=  room.getName();
            if (i % 5 != 0)
                output += " | ";
            ++i;
        }
        hotelPanel.addContentInfo(output);
    }

    private void guiLowLevelRoom(){
        PanelViewHotel viewPanel = this.gui.getPanelViewHotel();
        Hotel hotel = getHotelPanelSelectedHotel(viewPanel);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }
        viewPanel.setContentInfo("");
        guiDisplayRoomSelection(viewPanel, hotel.getName(), hotel.getRoomList());

        String roomName = viewPanel.getTfRoomName().getText();
        if (roomName.isEmpty()) {
            return;
        }

        Room room = model.getRoomOfAHotel(hotel.getName(), roomName);
        if (room == null) {
            viewPanel.setContentInfo("Room does not exist. \nInput a valid room name, or empty the textfield to go back.");
            return;
        }
        String output = "Name: " + room.getName();
        output += "\nType: " + model.getRoomTypeString(room) + "\n" + "To go back to the list, please empty the View Room Textfield and press again.";
        viewPanel.setContentInfo(output);
    }

    private void guiLowLevelReservation(){
        PanelViewHotel viewPanel = this.gui.getPanelViewHotel();
        Hotel hotel = getHotelPanelSelectedHotel(viewPanel);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }
        viewPanel.setContentInfo("");
        guiDisplayRoomSelection(viewPanel, hotel.getName(), hotel.getRoomList());
        String roomName = viewPanel.getTfRoomName().getText();
        if (roomName.isEmpty()) {
            return;
        }

        Room room = model.getRoomOfAHotel(hotel.getName(), roomName);
        if (room == null) {
            viewPanel.setContentInfo("Room does not exist. \nInput a valid room name, or empty the textfield to go back.");
            return;
        }

        ArrayList<Reservation> reservationList = model.filterHotelReservationsByRoom(hotel, room);
        viewPanel.setContentInfo("\nSelected hotel: " + hotel.getName() + "\nSelected room: " + roomName);
        String output = "";
        if (reservationList.isEmpty())
            viewPanel.setContentInfo("\nNo reservation currently exists.");
        else {
            viewPanel.addContentInfo("\nList of reservations:");
            int i = 1;
            for (Reservation reservation : reservationList) {
                viewPanel.addContentInfo(i + ".)" + "\n");
                viewPanel.addContentInfo("Check-in date: " + reservation.getCheckInDate() + "\n");
                viewPanel.addContentInfo("Room: " + reservation.getRoom() + "\n");
                ++i;
            }
        }
        viewPanel.setContentInfo(output);

        int daySelection = viewPanel.getjLDateSelected().getSelectedIndex();
        if (daySelection == -1){
            viewPanel.setContentInfo("Select a date.");
            return;
        }

        LocalDate date = LocalDate.of(2024, View.SYSTEM_MONTH, viewPanel.getjLDateSelected().getSelectedValue());
        Reservation reservation = model.getReservation(hotel.getName(), roomName, date);

        if (reservation == null) {
            viewPanel.setContentInfo("No reservation on room " + roomName + "." + "\nTo go back to the list, please empty the View Room Textfield and press again.");
            return;
        }

        String guestName = reservation.getGuestName();
        int checkInDay = reservation.getCheckInDate().getDayOfMonth();
        int checkOutDay = reservation.getCheckOutDate().getDayOfMonth();
        String priceBreakdown = model.getReservationPriceBreakdown(hotel, reservation); 
        double totalPrice = model.getReservationTotalPrice(hotel, reservation);

        viewPanel.addContentInfo("Guest: " + guestName);
        viewPanel.addContentInfo("Check-in: July " + checkInDay + ", 2024");
        viewPanel.addContentInfo("Check-out: July " + checkOutDay + ", 2024");
        viewPanel.addContentInfo("Price breakdown:" + priceBreakdown);
        viewPanel.addContentInfo("Total price: " + totalPrice);

        viewPanel.setContentInfo(output);
    }

    private void guiCreateHotel(){
        PanelCreateHotel createHotel = gui.getPanelCreateHotel();
        String strHotelNameField = createHotel.getNameField().getText();
        Hotel hotel = model.getHotelClone(strHotelNameField);
        Boolean hasExistingName = hotel != null; /* hotel does not exist yet */

        if (hasExistingName || strHotelNameField.equals("")){
            createHotel.setContentInfo("Please insert a valid hotel name.");
            return;
        } 

        String strPriceFieldText = gui.getPanelCreateHotel().getPriceField().getText();
        Double hotelBasePrice = 0.0;

        try {
            hotelBasePrice = Double.parseDouble(strPriceFieldText);
        } catch (NumberFormatException e) {
            gui.getPanelCreateHotel().setContentInfo("Please insert a valid price.");
            return;
        }

        if (hotelBasePrice < 100.0 || !Double.isFinite(hotelBasePrice)){
            gui.getPanelCreateHotel().setContentInfo("Price should be finite and greater than P100.0.");
            return;
        }

        if (hasUserNotConfirmed(createHotel, "Create Hotel Instance")){
            return;
        }

        if (hasUserNotAgreed(createHotel, "Create Hotel Instance")){
            createHotel.setContentInfo("Cancelled creation.");
            return;
        }

        hasUserAgreed(createHotel);
        
        Result hotelResult = model.addHotel(strHotelNameField, hotelBasePrice);
        System.out.println(hotelResult.getMessage());

        createHotel.setContentInfo("Hotel succesfully created.");
        createHotel.getNameField().setText("");
        createHotel.getPriceField().setText("");
        guiUpdateHotelSelection();
    }

    private void refreshPanel(){
        ArrayList<Hotel> currentHotelList = model.getHotelList();
        int currentPanelIndex = gui.getViewedPanel() - 1;
        PanelHotelSelection hotelSelectionPanel = this.gui.getPanelHotelSelectionList().get(currentPanelIndex);
        
        if (hasSelectedHotel[currentPanelIndex]){
            hotelSelectionPanel.setBtnPrevHotelEnable(false);
            hotelSelectionPanel.setBtnNextHotelEnable(false);
        } else {
            hotelSelectionPanel.setBtnPrevHotelEnable(selectedHotels[currentPanelIndex] >= 1);
            hotelSelectionPanel.setBtnNextHotelEnable(selectedHotels[currentPanelIndex] <= currentHotelList.size() - 2);
        }
        hotelSelectionPanel.setBtnSelectHotel(!this.hasConfirmed);
        hotelSelectionPanel.setSelectionVisible(hasSelectedHotel[currentPanelIndex]);
    }

    private void guiUpdateHotelSelection(){
        ArrayList<Hotel> currentHotelList = model.getHotelList();
        String listString = new String("");
        ArrayList<PanelHotelSelection> panelList = this.gui.getPanelHotelSelectionList();
        
        refreshPanel();

        if (currentHotelList.isEmpty()){
            
            for (PanelHotelSelection hotelSelection : panelList){
                hotelSelection.setSelectedHotel("None selected");
                hotelSelection.setHotelList("No hotels created.");
                hotelSelection.setBtnSelectHotel(false);
                hotelSelection.setBtnNextHotelEnable(false);
                hotelSelection.setBtnPrevHotelEnable(false);
            }
            
            return;
        }
        
        int i = 1;
        for (Hotel currHotel : currentHotelList){
            listString += "" + i + ") " + currHotel.getName() + "\n";
            i++;
        }
        System.out.println(listString);
        i = 0;
        for (PanelHotelSelection hotelSelection : panelList){
            selectedHotels[i] = 0;
            hotelSelection.setHotelList(listString);
            hotelSelection.setBtnSelectHotel(true);
            hotelSelection.setSelectedHotel(currentHotelList.getFirst().getName());
            hotelSelection.setBtnPrevHotelEnable(false);
            hotelSelection.setBtnNextHotelEnable(currentHotelList.size() != 1);
            i++;

        }
    }

    private void getPrevHotel(){
        ArrayList<Hotel> currentHotelList = model.getHotelList();
        int currentPanelIndex = gui.getViewedPanel() - 1;
        PanelHotelSelection hotelSelectionPanel = this.gui.getPanelHotelSelectionList().get(currentPanelIndex);
        System.out.println(selectedHotels[currentPanelIndex] + currentHotelList.get(selectedHotels[currentPanelIndex]).getName());
        hotelSelectionPanel.setBtnPrevHotelEnable(selectedHotels[currentPanelIndex] > 1);
        hotelSelectionPanel.setBtnNextHotelEnable(true);
        selectedHotels[currentPanelIndex] -= 1;
        hotelSelectionPanel.setSelectedHotel(currentHotelList.get(selectedHotels[currentPanelIndex]).getName());
    }

    private Hotel getHotelPanelSelectedHotel(HotelPanel panel){
        ArrayList<Hotel> currentHotelList = model.getHotelList();
        int currentPanelIndex = gui.getViewedPanel() - 1;

        if (currentHotelList.isEmpty() || !hasSelectedHotel[currentPanelIndex]){
            panel.setContentInfo("No hotel selected.");
            return null;
        } else 

        return currentHotelList.get(selectedHotels[currentPanelIndex]);
    }

    private boolean hasUserNotConfirmed(HotelPanel hotelPanel, String buttonName) {
        System.out.println(this.hasConfirmed);
        Boolean hasNotConfirmed = !this.hasConfirmed;
        if (hasNotConfirmed) {
            gui.setOnlyPanelEnable(hotelPanel.getPanelName());
            hotelPanel.setAllButtonsDisabled();
            hotelPanel.setConfirmationPanelVisilibity(true);
            hotelPanel.setHotelButtonsEnabled(false);
            this.actionToConfirm = buttonName;
            this.hasConfirmed = true;
            System.out.println("bus\n");
        }
        return hasNotConfirmed;
    }

    private boolean hasUserNotAgreed(HotelPanel hotelPanel, String buttonName){
        System.out.println(this.hasAgreed);
        Boolean hasUserNotAgreed = !this.hasAgreed;
        if (hasUserNotAgreed) {
            hotelPanel.setContentInfo("Cancelled creation.");
            hotelPanel.resetButtonEnabled();
            hotelPanel.setConfirmationPanelVisilibity(false);
            this.actionToConfirm = "";
            resetUserAgreement();
            confirmSelectedHotel();
            gui.resetPanelEnable(); 
        }
        return hasUserNotAgreed;
    }

    private boolean hasUserAgreed(HotelPanel hotelPanel){
        System.out.println(this.hasAgreed);
        Boolean hasUserAgreed = this.hasAgreed;
        if (this.hasAgreed){
            hotelPanel.setConfirmationPanelVisilibity(false);
            hotelPanel.resetButtonEnabled();
            this.actionToConfirm = "";
            resetUserAgreement();
            confirmSelectedHotel();
            gui.resetPanelEnable();
        }

        return hasUserAgreed;
    }

    private void confirmSelectedHotel(){
        int currentPanelIndex = gui.getViewedPanel() - 1;
        hasSelectedHotel[currentPanelIndex] = !hasSelectedHotel[currentPanelIndex];
        refreshPanel();
    }

    private void getNextHotel(){
        ArrayList<Hotel> currentHotelList = model.getHotelList();
        int currentPanelIndex = gui.getViewedPanel() - 1;
        PanelHotelSelection hotelSelectionPanel = this.gui.getPanelHotelSelectionList().get(currentPanelIndex);
        // TODO: Remove SYSTEM.out
        System.out.println(selectedHotels[currentPanelIndex] + currentHotelList.get(selectedHotels[currentPanelIndex]).getName());
        hotelSelectionPanel.setBtnPrevHotelEnable(true);
        hotelSelectionPanel.setBtnNextHotelEnable(selectedHotels[currentPanelIndex] < currentHotelList.size() - 2);
        selectedHotels[currentPanelIndex] += 1;

        hotelSelectionPanel.setSelectedHotel(currentHotelList.get(selectedHotels[currentPanelIndex]).getName());
    }

    private void resetUserAgreement(){
        this.hasConfirmed = false;
        this.hasAgreed = false;
    }

    public void displayReservationInformation(HotelPanel hotelPanel, ArrayList<Reservation> reservationList) {
        if (reservationList.isEmpty()) {
            hotelPanel.addContentInfo("\nNo reservations currently exist.");
        } else {
            int i = 1;
            for (Reservation reservation : reservationList) {
                hotelPanel.addContentInfo(i + ".)");
                hotelPanel.addContentInfo("Guest: " + reservation.getGuestName());
                hotelPanel.addContentInfo("Room: " + reservation.getRoom().getName());
                hotelPanel.addContentInfo("Check-in: " + reservation.getCheckInDate());
                hotelPanel.addContentInfo("Check-out: " + reservation.getCheckOutDate());
                ++i;
            }
        }
    }

    private void guiChangeName(){
        PanelManageHotel manageHotel = gui.getPanelManageHotel();
        Hotel hotel = getHotelPanelSelectedHotel(manageHotel);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }

        String wantedName = manageHotel.getTfChangeName();
        if (model.doesHotelExist(wantedName)){
            manageHotel.setContentInfo("Name already exists. Please provide a unique name");
            return;
        } else if (wantedName.equals("")){
            manageHotel.setContentInfo("New name is empty, please provide a name.");
            return;
        }

        if (hasUserNotConfirmed(manageHotel, "Change Name"))
            return;
       
        if (hasUserNotAgreed(manageHotel, "Change Name")){
            manageHotel.setContentInfo("Cancelled name change.");
            return;
        }
        hasUserAgreed(manageHotel);
        
        manageHotel.setContentInfo("Changed the name " + hotel.getName() + " to " + wantedName);
        model.setHotelName(hotel.getName(), wantedName);
        
        guiUpdateHotelSelection();
    }

    private void guiAddRooms(){
        PanelManageHotel manageHotel = gui.getPanelManageHotel();
        Hotel hotel = getHotelPanelSelectedHotel(manageHotel);
        boolean isHotelNull = hotel == null;

        if (isHotelNull){
            return;
        }
    
        String wantedName = manageHotel.getTfAddRoomName();    
        
        if (wantedName.equals("")){
            manageHotel.setContentInfo("");
            guiDisplayRoomSelection(manageHotel, hotel.getName(), hotel.getRoomList());
            return;
        }

        if (hasUserNotConfirmed(manageHotel, "Add Rooms"))
            return;
       
        if (hasUserNotAgreed(manageHotel, "Add Rooms"))
            return;

        hasUserAgreed(manageHotel);
        Result resAddRoom = model.addRoomToAHotel(hotel.getName(), wantedName, Room.ROOM_TYPE.STANDARD);
        
        if (resAddRoom.isSuccesful()){
            manageHotel.setContentInfo("Succesfully added a Room.");
            guiUpdateHotelSelection();
            return;
        }

        switch(resAddRoom.getCommonError()){
            case ER_MAX_CAPACITY:
                manageHotel.setContentInfo("Hotel is at max capacity.");
                break;
            case ER_NO_HOTEL:
                manageHotel.setContentInfo("Name of hotel does not exist.");
                break;
            case ER_NOT_UNIQUE_GIVENNAME:
                manageHotel.setContentInfo("Room name is not unique.");
                break;
            default:
                manageHotel.setContentInfo("Unknown Error: " + resAddRoom.getMessage());
                break;
        }    
    }

    private void guiRemoveRooms(){
        PanelManageHotel manageHotel = gui.getPanelManageHotel();
        Hotel hotel = getHotelPanelSelectedHotel(manageHotel);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }

        String wantedName = manageHotel.getTfRemoveRoomName();    
        
        if (wantedName.equals("")){
            manageHotel.setContentInfo("Please provide the name of the room you want to DELETE:");
            guiDisplayRoomSelection(manageHotel, hotel.getName(), hotel.getRoomList());
            return;
        }

        if (hasUserNotConfirmed(manageHotel, "Remove Rooms"))
            return;
       
        if (hasUserNotAgreed(manageHotel, "Remove Rooms"))
            return;

        hasUserAgreed(manageHotel);


        Result resRemoveRoom = model.removeRoomOfHotel(hotel.getName(), wantedName);
        if (resRemoveRoom.isSuccesful()){
            manageHotel.setContentInfo("Room successfully deleted.");
            return;
        }
        switch (resRemoveRoom.getCommonError()) {
            case ER_NO_ROOM:
                manageHotel.setContentInfo("Room not found.");
                break;
            case ER_ROOM_HAS_RESERVATION:
                manageHotel.setContentInfo("Cannot delete, has a reservation.");
                break;
            default:
                manageHotel.setContentInfo("Unknown Error: " + resRemoveRoom.getMessage());
                break;
        }

    }

    private void guiUpdatePrice(){
        PanelManageHotel manageHotel = gui.getPanelManageHotel();
        Hotel hotel = getHotelPanelSelectedHotel(manageHotel);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }

        double price = 0.0;
        Result errorState;


        String tfPrice = manageHotel.getTfUpdatePrice();

        try {
            price = Double.parseDouble(tfPrice);
        } catch (NumberFormatException e) {
            manageHotel.setContentInfo("Please insert a valid price.");
            return;
        }

        if (price < 100.0 || !Double.isFinite(price)){
            manageHotel.setContentInfo("Price should be finite and greater than P100.0.");
            return;
        }


        if (hasUserNotConfirmed(manageHotel, "Update Price"))
            return;
       
        if (hasUserNotAgreed(manageHotel, "Update Price")){
            manageHotel.setContentInfo("Cancelled price change.");
            return;
        }
        hasUserAgreed(manageHotel);

        errorState = model.setHotelBasePrice(hotel.getName(), price);

        if (errorState.isSuccesful()) {
            manageHotel.setContentInfo("Price succesfully changed");
            return;
        }

        switch (errorState.getCommonError()) {
            case ER_NO_HOTEL:
                manageHotel.setContentInfo("Hotel not found.");
                break;
            case ER_ROOM_HAS_RESERVATION:
                manageHotel.setContentInfo("At least one reservation is made for this room; modification of price is not possible.");
                break;
            case ER_LOWER_THAN_BASEPRICE:
                manageHotel.setContentInfo("Price is lower than 100. Please Change to a higher than the minimum price.");
                break;
            default:
                manageHotel.setContentInfo("Unknown Error: " + errorState.getMessage());
                break;
        }
    }

    private void guiRemoveReservation(){
        PanelManageHotel manageHotel = gui.getPanelManageHotel();
        Hotel hotel = getHotelPanelSelectedHotel(manageHotel);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }

        Result resRemoveReservation;

        String roomName = manageHotel.getTfRemoveReservation();

        if (roomName.equals("")){
            displayReservationInformation(manageHotel, model.getReservations(hotel.getName()));
            return;
        }

        if (hasUserNotConfirmed(manageHotel, "Remove Reservation"))
            return;
       
        if (hasUserNotAgreed(manageHotel, "Remove Reservation")){
            manageHotel.setContentInfo("Cancelled price change.");
            return;
        }
        hasUserAgreed(manageHotel);

        // TODO: Add a date namefield on manage hotel's guiRemoveReservation, and hook it up to this.
        resRemoveReservation = model.removeReservation(hotel.getName(), roomName, LocalDate.of(View.SYSTEM_YEAR, View.SYSTEM_MONTH, 1)); /* placeholder */

        if (resRemoveReservation.isSuccesful()) {
            manageHotel.setContentInfo("Reservation successfully deleted.");
        } else {
            switch (resRemoveReservation.getCommonError()) {
                case ER_NO_HOTEL:
                    manageHotel.setContentInfo("Hotel is not found");
                    break;
                case ER_NO_RESERVATION:
                    manageHotel.setContentInfo("Reservation is not found");
                    break;
                default:
                    manageHotel.setContentInfo("Unknown Error: " + resRemoveReservation.getMessage());
                    break;
            }
        }
        
    }

    private void guiRemoveHotel(){
        PanelManageHotel manageHotel = gui.getPanelManageHotel();
        Hotel hotel = getHotelPanelSelectedHotel(manageHotel);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }

        if (hasUserNotConfirmed(manageHotel, "Remove Hotel"))
            return;
       
        if (hasUserNotAgreed(manageHotel, "Remove Hotel")){
            manageHotel.setContentInfo("Cancelled remove hotel.");
            return;
        }
        hasUserAgreed(manageHotel);
        
        Boolean hasDelete = model.removeHotel(hotel.getName());

        if (hasDelete) {
            manageHotel.setContentInfo("Successfully removed hotel.");
            guiUpdateHotelSelection();
        } else
            manageHotel.setContentInfo("Hotel name cannot be found.");
    }

    /**
     * Displays the list of rooms with their details.
     * @param roomList the list of rooms to display
     */
    public void displayRoomList(HotelPanel hotelPanel, Hotel hotel, ArrayList<Room> roomList) {
        if(roomList.isEmpty()){
            hotelPanel.addContentInfo("No Rooms currently exist.");
            return;
        } 

        int i = 0;
        for (Room room : roomList) {
            double basePricePerNight = hotel.getBasePricePerNight();
            double multiplier = switch(room) {
                case StandardRoom r -> hotel.getStandardMultiplier();
                case DeluxeRoom r -> hotel.getDeluxeMultiplier();
                case ExecutiveRoom r -> hotel.getExecutiveMultiplier();
                case null, default -> 0f;
            };

            hotelPanel.addContentInfo(i + 1 + ".)");
            assert room != null;
            hotelPanel.addContentInfo("Room: " + room.getName());
            hotelPanel.addContentInfo("Class: " + room.getClass());
            hotelPanel.addContentInfo("Base Price: ₱" + (basePricePerNight * multiplier));
            ++i;
        }
    }
        

    private void guiBookReservation(){
        PanelBookReservation bookReservation = gui.getPanelBookReservation();
        Hotel hotel = getHotelPanelSelectedHotel(bookReservation);
        boolean isHotelNull = hotel == null;
        if (isHotelNull){
            return;
        }

        Room room = new StandardRoom(""); /* placeholder */
        ArrayList<Room> listOfAvailableRooms = new ArrayList<Room>();


        JList<Integer> jListStart = bookReservation.getJListDatesStart();
        JList<Integer> jListEnd = bookReservation.getJListDatesEnd();

        Boolean emptyDateSelection = jListStart.getSelectedIndex() == -1 || jListEnd.getSelectedIndex() == -1;
        bookReservation.setContentInfo("");
        if (emptyDateSelection) {
            bookReservation.addContentInfo("Please select a date on the starting and end datelist.");
            return;
        }
        
        LocalDate startDate = LocalDate.of(SYSTEM_YEAR, SYSTEM_MONTH, jListStart.getSelectedValue());
        LocalDate endDate = LocalDate.of(SYSTEM_YEAR, SYSTEM_MONTH, jListEnd.getSelectedValue());

        bookReservation.addContentInfo("Using the dates from " + startDate + " to " + endDate);
        listOfAvailableRooms = model.getAvailableRoomsByDate(hotel, startDate);
       
        
        String roomName = bookReservation.getRoomName();
        Room roomToReserve = model.getAvailableRoomByDateAndRoom(hotel, endDate, roomName);
        Boolean roomDoesNotExist = roomToReserve == null;
        if (roomDoesNotExist){
            bookReservation.addContentInfo("Please input a valid input room name.");
            displayRoomList(bookReservation, hotel, listOfAvailableRooms);
            return;
        }
        bookReservation.addContentInfo("Reserving room " + roomName + "");    


        String guestName = bookReservation.getGuestName();

        if (guestName.trim().equals("")){
            bookReservation.addContentInfo("Input a name with at least one non-white-space character.");
            return;
        }

        bookReservation.addContentInfo("\nHotel name: " + hotel.getName());
        bookReservation.addContentInfo("Guest name: " + guestName);
        bookReservation.addContentInfo("Check In: " +  startDate);
        bookReservation.addContentInfo("Check Out: " + endDate);
        bookReservation.addContentInfo("Room: " + room.getName() + " " + model.getRoomBasePricePerNight(hotel, room) + " PHP per night");

        if (hasUserNotConfirmed(bookReservation, "Book Reservation"))
            return;
       
        if (hasUserNotAgreed(bookReservation, "Book Reservation")){
            bookReservation.setContentInfo("Cancelled booking a reservation.");
            return;
        }
        hasUserAgreed(bookReservation);


        model.makeReservation(hotel.getName(), guestName, startDate,endDate, room);
    }

    /**
     * GUI ABOVE
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * CONSOLE BELOW
    */
    
    /**
     * Repeatedly prompts the user for a hotel name until they provide a valid one or quit.
     *
     * @return null if the user quit midway; the hotel instance, otherwise
     */
    public Hotel repeatPromptHotelName() {
        String hotelName;
        Hotel hotel;
        boolean isValidHotelName;
        do {
            hotelName = view.getInputStr("\nEnter the name of the hotel: ");
            if (hotelName.equalsIgnoreCase("quit")) {
                view.displayResultMessage("Hotel creation cancelled.");
                return null;
            }
            hotel = model.getHotelClone(hotelName);
            isValidHotelName = hotel == null; /* hotel does not exist yet */
            view.displayInvalidInputWarning(isValidHotelName, "Please provide a unique hotel name!");
        } while(!isValidHotelName);
        return hotel;
    }

    /**
     * Repeatedly prompts the user for a base price per night until they provide a valid one or quit.
     *
     * @return 0 if the user quit midway; the base price per night, otherwise
     */
    public double repeatPromptHotelBasePrice() {
        String buffer;
        double hotelBasePrice;
        boolean isValidBasePrice;
        do {
            buffer = view.getInputStr("\nEnter the base price per night for each room: ");
            if (buffer.equalsIgnoreCase("quit")) {
                view.displayResultMessage("Hotel creation cancelled.");
                return 0;
            }
            try {
                hotelBasePrice = Double.parseDouble(buffer);
                isValidBasePrice = hotelBasePrice >= 100;
                view.displayInvalidInputWarning(isValidBasePrice, "Please provide a base price greater than or equal to 100!");
            } catch(NumberFormatException e) {
                hotelBasePrice = 0;
                isValidBasePrice = false;
                view.displayInvalidInputWarning(isValidBasePrice, "Please provide a numeric response!");
            }
        } while(!isValidBasePrice);
        return hotelBasePrice;
    }

    /**
     * Creates a new hotel with a unique name and a base price for its rooms.
     * Prompts the user for input and handles the creation process.
     */
    public void createHotel() {
        view.displayCreateHotelPrompt();
        view.displayHotelSelection(model.getHotelList());

        Hotel hotel = repeatPromptHotelName();
        if (null == hotel) { /* user quit */
            return;
        }

        if (!view.confirmUserAction("creating a new hotel")) {
            view.displayResultMessage("Hotel creation cancelled.");
            return;
        }

        double hotelBasePrice = repeatPromptHotelBasePrice();
        if (hotelBasePrice == 0) { /* user quit */
            return;
        }

        /* hotel is successfully created */
        
        model.setHotelBasePrice(hotel.getName(), hotelBasePrice);
        view.displayResultMessage("Hotel creation successful! :>");
    }


    /**
     * Repeatedly prompts the user for a number from 1-3 corresponding to the hotel information they want to view
     *
     * @return '0' if the user quit midway; a char from 1-3, otherwise
     */
    public char repeatPromptHotelInfo() {
        String buffer;
        char userInput;
        boolean isValidInput;
        do {
            buffer = view.getInputStr("\nEnter a response (1/2/3): ");
            if (buffer.equalsIgnoreCase("quit")) {
                view.displayResultMessage("Hotel information viewing cancelled.");
                return '0';
            }
            userInput = buffer.charAt(0);
            isValidInput = userInput >= '1' && userInput <= '3';
            view.displayInvalidInputWarning(isValidInput, "Please provide a valid response!");
        } while (!isValidInput);
        return userInput;
    }

    /**
     * Repeatedly prompts the user for a day in the system month.
     *
     * @return an integer from 1-31; 0 if the user quit midway
     */
    public int repeatPromptDayOfMonth() {
        String buffer;
        int day;
        boolean isValidDay;
        view.displayMessage("\nThe system month is July.");
        do {
            buffer = view.getInputStr("\nEnter a day in July (1-31): ");
            if (buffer.equalsIgnoreCase("quit")) {
                view.displayResultMessage("Hotel information viewing cancelled.");
                return 0;
            }
            try {
                day = Integer.parseInt(buffer);
                isValidDay = day >= 1 && day <= 31;
                view.displayInvalidInputWarning(isValidDay, "Please provide a valid day!");
            } catch(NumberFormatException e) {
                day = 0;
                isValidDay = false;
                view.displayInvalidInputWarning(isValidDay, "Please provide an integer response!");
            }
        } while (!isValidDay);
        return day;
    }

    /**
     * Repeatedly prompts the user for a valid room name.
     *
     * @return the room name; "" if the user quits midway
     */
    public String repeatPromptRoomName(String hotelName) {
        String roomName;
        Room room;
        boolean isValidRoom;
        do {
            roomName = view.getInputStr("\nEnter the room name: ").toUpperCase();
            if (roomName.equalsIgnoreCase("quit")) {
                view.displayResultMessage("Hotel information viewing cancelled.");
                return "";
            }
            room = model.getRoomOfAHotel(hotelName, roomName);
            isValidRoom = room != null;
            view.displayInvalidInputWarning(isValidRoom, "Please provide a valid room name!");
        } while (!isValidRoom);
        return roomName;
    }

    /**
     * Repeatedly prompts the user for a valid check-in day.
     *
     * @return the check in day; 0 if the user quits midway
     */
    public int repeatPromptReservationCheckInDay() {
        String buffer;
        int day;
        boolean isValidDay;
        do {
            buffer = view.getInputStr("\nEnter the check-in day of the reservation (1-31): ");
            if (buffer.equalsIgnoreCase("quit")) {
                view.displayResultMessage("Hotel information viewing cancelled.");
                return 0;
            }
            try {
                day = Integer.parseInt(buffer);
                isValidDay = day >= 0 && day <= 31;
                view.displayInvalidInputWarning(isValidDay, "Please provide a valid day!");
            } catch(NumberFormatException e) {
                day = 0;
                isValidDay = false;
                view.displayInvalidInputWarning(isValidDay, "Please provide a numeric response!");
            }
        } while (!isValidDay);
        return day;
    }

    /**
     * Displays detailed information about a specified hotel, including room and reservation details.
     * Prompts the user for input and handles the information viewing process.
     *
     * @param hotel the hotel for which to display information
     */
    public void viewLowLevelInfo(Hotel hotel) {

        view.displayViewHotelPrompt();
        view.displayLowLevelHotelInfoPrompt(true, hotel.getName());

        char userInput = repeatPromptHotelInfo();

        view.clearScreen();
        view.displayViewHotelPrompt();

        String roomName;
        Room room;
        int day;
        LocalDate date;

        switch(userInput) {
            case View.SELECTED_DATE_OPTION: // selected date hotel availability

                day = repeatPromptDayOfMonth();
                if (0 == day) {
                    return;
                }
                date = LocalDate.of(2024, View.SYSTEM_MONTH, day);
                int availableRooms = model.getNumOfAvailableRoomsByDate(hotel, date);

                view.clearScreen();
                view.displaySelectedDateInfo(hotel.getName(), availableRooms, hotel.getTotalRooms() - availableRooms);
                view.displayResultMessage("Hotel information viewing successful! :>");
                break;

            case View.SELECTED_ROOM_OPTION: // selected room information

                view.displayRoomSelection(hotel.getName(), hotel.getRoomList());
                view.displayDivider();

                roomName = repeatPromptRoomName(hotel.getName());
                if (roomName.isEmpty()) {
                    return;
                }
                room = model.getRoomOfAHotel(hotel.getName(), roomName);

                view.clearScreen();
                view.displaySelectedRoomInfo(room.getName(), model.getRoomTypeString(room));
                view.displayResultMessage("Hotel information viewing successful! :>");
                break;

            case View.SELECTED_RESERVATION_OPTION: // selected reservation information

                view.displayRoomSelection(hotel.getName(), hotel.getRoomList());
                view.displayDivider();

                roomName = repeatPromptRoomName(hotel.getName());
                if (roomName.isEmpty()) {
                    return;
                }
                room = model.getRoomOfAHotel(hotel.getName(), roomName);

                view.clearScreen();
                view.displayViewHotelPrompt();
                view.displayReservationSelection(hotel.getName(), roomName, model.filterHotelReservationsByRoom(hotel, room));

                day = repeatPromptReservationCheckInDay();
                if (0 == day) {
                    return;
                }
                date = LocalDate.of(2024, View.SYSTEM_MONTH, day);
                Reservation reservation = model.getReservation(hotel.getName(), roomName, date);

                String guestName = reservation.getGuestName();
                int checkInDay = reservation.getCheckInDate().getDayOfMonth();
                int checkOutDay = reservation.getCheckOutDate().getDayOfMonth();
                String priceBreakdown = model.getReservationPriceBreakdown(hotel, reservation); // revise
                double totalPrice = model.getReservationTotalPrice(hotel, reservation);

                view.clearScreen();
                view.displaySelectedReservationInfo(guestName, checkInDay, checkOutDay, priceBreakdown, totalPrice);
                view.displayResultMessage("Hotel information viewing successful! :>");
                break;
        }
    }


    /**
     * Allows the user to view high-level or low-level information about a selected hotel.
     * Prompts the user for input and handles the information viewing process.
     */
    public void viewHotel() {

        view.displayViewHotelPrompt();
        view.displayHotelSelection(model.getHotelList());

        String hotelName;
        Hotel hotel;
        boolean isValidHotel;
        do {
            hotelName = view.getInputStr("\nEnter the name of the hotel you want to view: ");
            if (hotelName.equalsIgnoreCase("quit")) {
                view.displayResultMessage("Hotel information viewing cancelled.");
                return;
            }
            hotel = model.getHotelClone(hotelName);
            isValidHotel = hotel != null;
            view.displayInvalidInputWarning(isValidHotel, "Please provide a valid hotel name!");
        } while (!isValidHotel);

        view.clearScreen();
        view.displayViewHotelPrompt();
        view.displayHotelInfoPrompt(hotelName);

        String buffer;
        char userInput;
        boolean isValidInput;
        do {
            buffer = view.getInputStr("\nEnter a response (H/L): ");
            if (buffer.equalsIgnoreCase("quit")) {
                view.displayResultMessage("Hotel information viewing cancelled.");
                return;
            }
            userInput = buffer.toUpperCase().charAt(0);
            isValidInput = userInput == View.HIGH_LEVEL_OPTION || userInput == View.LOW_LEVEL_OPTION;
            view.displayInvalidInputWarning(isValidInput, "Please provide a valid response!");
        } while(!isValidInput);
        view.clearScreen();
        switch(userInput) {
            case View.HIGH_LEVEL_OPTION:
                double estimatedEarnings = model.getHotelEstimatedEarnings(hotel);
                view.displayHighLevelHotelInfo(hotel, estimatedEarnings);
                view.displayResultMessage("Hotel information viewing successful.");
                break;
            case View.LOW_LEVEL_OPTION:
                viewLowLevelInfo(hotel);
                break;
        }
        view.clearScreen();
    }

    /**
     * Changes the name of a hotel.
     * Displays a list of current hotels and prompts the user for the old and new names.
     * Confirms the action and updates the hotel's name if valid.
     */
    private void changeHotelName(Hotel oldHotelName) {
        ArrayList<Hotel> currentList;
        String newHotelName;
        Result resSetHotelName;

        view.clearScreen();
        currentList = model.getHotelList();
        view.displayHotelSelection(currentList);

        newHotelName = view.getInputStr("Please input a new name: ");

        if (!view.confirmUserAction("changing the hotel name")) {
            return;
        }

        view.displayMessage("\n");
        resSetHotelName = model.setHotelName(oldHotelName.getName(), newHotelName);

        if (resSetHotelName.isSuccesful()){
            view.displayResultMessage("Changed Hotel name.");
            oldHotelName = model.getHotelClone(newHotelName);
        } else {
            switch (resSetHotelName.getCommonError()) {
                case ER_EXISTING_OLD_NAME:
                    view.displayResultMessage("New give name is same as the previous old one.");
                    break;
                case ER_NO_HOTEL:
                    view.displayResultMessage("Hotel does not exist.");
                    break;
                case ER_NOT_UNIQUE_GIVENNAME:
                    view.displayResultMessage("Name is not unique, please provide a new one.");
                    break;
                default:
                    view.displayResultMessage("Unknown Error: " + resSetHotelName.getMessage());
                    break;
            }
        }
        view.clearScreen();
    }

    /**
     * Adds a room to a selected hotel.
     * Displays a list of current hotels and rooms, prompts the user for the hotel and room names.
     * Confirms the action and adds the room if valid.
     */
    private void addRooms(Hotel hotel) {
        ArrayList<Hotel> currentHotelList;
        ArrayList<Room> currentRoomList;

        String nameOfRoomToAdd;
        Result resAddRoom;

        currentHotelList = model.getHotelList();
        view.displayHotelSelection(currentHotelList);

        if (!model.doesHotelExist(hotel.getName())) {
            view.displayMessage(hotel.getName() + " does not exist.");
            return;
        }

        currentRoomList = model.getRoomListOfAHotel(hotel.getName());
        view.displayRoomList(hotel, currentRoomList);

        nameOfRoomToAdd = view.getInputStr("Please provide the name of the room you want to add: ");

        if (!view.confirmUserAction("adding a room to the selected hotel")) {
            view.pressEnterToContinue();
            view.clearScreen();
            return;
        }

        // TODO: Ask the user what type of room they want to add via buttons

        resAddRoom = model.addRoomToAHotel(hotel.getName(), nameOfRoomToAdd, Room.ROOM_TYPE.STANDARD); // placeholder

        if (resAddRoom.isSuccesful()){
            view.displayMessage("Successfully added a Room.");
            view.pressEnterToContinue();
            view.clearScreen();
            return;
        }

        switch (resAddRoom.getCommonError()){
            case ER_MAX_CAPACITY:
                view.displayMessage("Hotel already exists.");
                break;
            case ER_NO_HOTEL:
                view.displayMessage("Name of hotel does not exist.");
                break;
            case ER_NOT_UNIQUE_GIVENNAME:
                view.displayMessage("Room name not unique.");
                break;
            default:
                view.displayMessage("Unknown Error: " + resAddRoom.getMessage());
                break;
        }
        view.pressEnterToContinue();
        view.clearScreen();
    }

    /**
     * Removes a room from a selected hotel.
     * Displays a list of current hotels and rooms, prompts the user for the hotel and room names.
     * Confirms the action and removes the room if valid.
     */
    private void removeRooms(Hotel hotel) {
        ArrayList<Room> currentRoomList;

        String nameOfRoomToDelete = "";
        Result resRemoveRoom;

        if (!model.doesHotelExist(hotel.getName())) {
            view.displayMessage("Hotel does not exist.");
            return;
        }

        currentRoomList = model.getRoomListOfAHotel(hotel.getName());
        view.displayRoomList(hotel, currentRoomList);

        nameOfRoomToDelete = view.getInputStr("Please provide the name of the room you want to DELETE:");

        if (!view.confirmUserAction("deleting a room?")){
            view.clearScreen();
            return;
        }

        resRemoveRoom = model.removeRoomOfHotel(hotel.getName(), nameOfRoomToDelete);

        if (resRemoveRoom.isSuccesful()){
            view.displayResultMessage("Room successfully deleted.");
            return;
        }

        switch (resRemoveRoom.getCommonError()) {
            case ER_NO_ROOM:
                view.displayResultMessage("Room not found.");
                break;
            case ER_ROOM_HAS_RESERVATION:
                view.displayResultMessage("Cannot delete, has a reservation.");
                break;
            default:
                view.displayMessage("Unknown Error: " + resRemoveRoom.getMessage());
                break;
        }

        view.clearScreen();
    }

    /**
     * Updates the price of rooms in a selected hotel.
     * Displays a list of current hotels, prompts the user for the hotel name and new price.
     * Confirms the action and updates the price if valid.
     */
    private void updatePrice(Hotel nameOfHotel) {
        double price;
        Result errorState;

        Boolean hasNoExistingHotel = !model.doesHotelExist(nameOfHotel.getName());
        if (hasNoExistingHotel) {
            view.displayMessage("Hotel does not exist.");
            return;
        }

        price = view.getInputDouble("What is the new price? Price should be greater or equal 100.");

        Boolean isBelowBasePrice = price < 100;
        if (isBelowBasePrice) {
            view.displayResultMessage("Error, Price should be greater or equal 100.");
            view.clearScreen();
            return;
        }

        Boolean userWantsToStopUpdatingPrice = !view.confirmUserAction("updating the selected hotel's price per room>");
        if (userWantsToStopUpdatingPrice){
            return;
        }

        errorState = model.setHotelBasePrice(nameOfHotel.getName(), price);

        if (errorState.isSuccesful()) {
            view.displayResultMessage("Price succesfully changed");
            return;
        }

        switch (errorState.getCommonError()) {
            case ER_NO_HOTEL:
                view.displayResultMessage("Hotel not found.");
                break;
            case ER_ROOM_HAS_RESERVATION:
                view.displayResultMessage("At least one reservation is made for this room; modification of price is not possible.");
                break;
            case ER_LOWER_THAN_BASEPRICE:
                view.displayResultMessage("Price is lower than 100. Please Change to a higher than the minimum price.");
                break;
            default:
                view.displayMessage("Unknown Error: " + errorState.getMessage());
                break;
        }
    }

    /**
     * Removes a reservation from a selected hotel.
     * Prompts the user for the hotel and reservation names.
     * Confirms the action and removes the reservation if valid.
     */
    private void removeReservations(Hotel hotel) {
        String roomName;
        Result resRemoveReservation;

        if (!model.doesHotelExist(hotel.getName())) {
            view.displayMessage("Hotel does not exist.");
            return;
        }
        
        view.displayReservationInformation(model.getReservations(hotel.getName()));

        roomName = view.getInputStr("Input the room name of the reservation");

        if (!view.confirmUserAction("removing the selected reservation from the hotel")) {
            return;
        }

        // TODO: Prompt the user for the check-in date via GUI.

        resRemoveReservation = model.removeReservation(hotel.getName(), roomName, LocalDate.of(View.SYSTEM_YEAR, View.SYSTEM_MONTH, 1)); /* placeholder */

        if (resRemoveReservation.isSuccesful()) {
            view.displayResultMessage("Reservation successfully deleted.");
        } else {
            switch (resRemoveReservation.getCommonError()) {
                case ER_NO_HOTEL:
                    view.displayResultMessage("Hotel is not found");
                    break;
                case ER_NO_RESERVATION:
                    view.displayResultMessage("Reservation is not found");
                    break;
                default:
                    view.displayMessage("Unknown Error: " + resRemoveReservation.getMessage());
                    break;
            }
        }
        
    }

    /**
     * Removes a hotel.
     * Prompts the user for the hotel name.
     * Confirms the action and removes the hotel if valid.
     */
    private Boolean removeHotel(Hotel hotel){
        Boolean hasDelete = false;

        if (view.confirmUserAction("removing the selected hotel")){
            hasDelete = model.removeHotel(hotel.getName());

            if (hasDelete){
                view.displayMessage("Successfully removed hotel.");
            } else
                view.displayMessage("Hotel name cannot be found.");
        }

        return hasDelete;
    }

    /**
     * Manages hotel actions based on the given manager state.
     * Prompts the user for specific actions and executes them.
     *
     * @param manageState the current state of the manager
     */
    private void manageHotelActions(MANAGER_STATE manageState, Hotel hotel, Boolean hasDeleted) {
        boolean isUsingAFunction = true;
        while (isUsingAFunction) {
            view.displayManageHotelPrompt(manageState);

            if (!view.confirmUserAction("continue? Enter N to quit.")) {
                return;
            }

            switch (manageState) {
                case MS_CHANGE_NAME:
                    changeHotelName(hotel);
                    break;
                case MS_ADD_ROOMS:
                    addRooms(hotel);
                    break;
                case MS_REMOVE_ROOMS:
                    removeRooms(hotel);
                    break;
                case MS_UPDATE_PRICE:
                    updatePrice(hotel);
                    break;
                case MS_REMOVE_RESERVATIONS:
                    removeReservations(hotel);
                    break;
                case MS_REMOVE_HOTEL:
                    isUsingAFunction = removeHotel(hotel);
                    break;
                default:
                    view.displayMessage("Invalid Display state.");
                    break;
            }
        }
    }

    /*
     * Main method to manage hotels.
     * Prompts the user for actions and manages the flow of hotel management.
     */
    public void manageHotel() {
        Boolean isPerformingManagingHotel = true, isEnteringHotel = true, isManaging = true, hasDelete = false;
        MANAGER_STATE CurrentState = MS_OVERVIEW;

        Hotel newHotel = new Hotel("");

        while (isManaging){
            isEnteringHotel = true;
            isPerformingManagingHotel = true;

            while (isEnteringHotel) {
                view.displayManageHotelPrompt(MS_CHOSE_HOTEL);

                if (!view.confirmUserAction("managing the hotel?")) {
                    isEnteringHotel = false;
                    isManaging = false;
                    return;
                }

                newHotel = selectValidHotel();

                view.displayInvalidInputWarning(newHotel != null, "Please input a valid hotel name");

                if (newHotel != null && model.doesHotelExist(newHotel.getName()) && view.confirmUserAction("\ncontinue at managing hotel with the given name?")){
                    isEnteringHotel = false;
                    view.pressEnterToContinue();
                }

                view.clearScreen();
            }

            while (isPerformingManagingHotel) {
                view.displayManageHotelPrompt(MS_OVERVIEW);

                if (!hasDelete)
                    view.displayMessage("Editing Hotel: " + newHotel.getName());

                String userInput = view.getInputStr("Please provide a response: ");

                switch (userInput) {
                    case "q":
                        CurrentState = MS_OVERVIEW;
                        isPerformingManagingHotel = false;
                        break;
                    case "a":
                        CurrentState = MS_CHANGE_NAME;
                        break;
                    case "b":
                        CurrentState = MS_ADD_ROOMS;
                        break;
                    case "c":
                        CurrentState = MS_REMOVE_ROOMS;
                        break;
                    case "d":
                        CurrentState = MS_UPDATE_PRICE;
                        break;
                    case "e":
                        CurrentState = MS_REMOVE_RESERVATIONS;
                        break;
                    case "f":
                        CurrentState = MS_REMOVE_HOTEL;
                        break;
                    default:
                        CurrentState = MS_OVERVIEW;
                        view.displayInvalidInputWarning();
                }

                if (CurrentState != MS_OVERVIEW) {
                    manageHotelActions(CurrentState, newHotel, hasDelete);
                }

                if (hasDelete){
                    isPerformingManagingHotel = false;
                }

                view.clearScreen();
            }
        }
    }

    /**
     * Selects a valid hotel based on user input.
     * Displays a list of current hotels and prompts the user for the hotel name.
     * Returns the selected hotel if valid.
     *
     * @return the selected hotel, or null if the hotel does not exist
     */
    private Hotel selectValidHotel() {
        ArrayList<Hotel> listOfHotels;
        String nameOfHotel;

        listOfHotels = model.getHotelList();
        view.displayHotelSelection(listOfHotels);
        nameOfHotel = view.getInputStr("Please provide the name of the hotel you want to change:");

        if (!model.doesHotelExist(nameOfHotel)) {
            view.displayMessage("Hotel does not exist.");
            return null;
        }

        return model.getHotelClone(nameOfHotel);
    }

    /**
     * Prompts the user to input valid check-in and check-out dates.
     * Displays a date selection prompt and ensures the check-in date is before the check-out date.
     * Returns the selected dates as an ArrayList of LocalDate.
     *
     * @return an ArrayList of LocalDate containing the check-in and check-out dates
     */
    private ArrayList<LocalDate> selectValidCheckInAndCheckOutDates() {
        Boolean isSelectingDate = true;

        ArrayList <LocalDate> result = new ArrayList<LocalDate>();

        while (isSelectingDate) {
            view.displayBookReservationPrompt(SB_DATE_SELECTION);

            result.add(view.getLocalDate("Please input the check-in date:"));
            result.add(view.getLocalDate("Please input the check-out date:"));
            if (result.get(0).isAfter(result.get(1))){
                view.displayMessage("Check-in-date is AFTER Check-out-date.");
                view.displayInvalidInputWarning();
            } else {
                view.displayMessage("Valid Date.");
                isSelectingDate = false;
            }

        }

        return result;
    }

    /**
     * Manages the process of booking a reservation.
     * Prompts the user for hotel selection and valid check-in and check-out dates.
     * Retrieves the total available rooms by date for the selected hotel.
     */
    public void bookReservation() {
        String checkInDate = new String(), checkOutDate = new String(), guestName = new String();
        Boolean isReserving = true, isPerformingBookReservation = true, isInputtingRoom = true, isInputtingName = true, isChoosingHotel = true, isChoosingRoom = true;

        int roomChoice = 0;
        /* TODO: Prompt the user to select the room type via GUI. */
        Room room = new StandardRoom(""); /* placeholder */
        ArrayList<Room> listOfAvailableRooms = new ArrayList<Room>();
        Hotel validHotel = new Hotel("");

        ArrayList<LocalDate> arrayLocalDatesCheckInCheckOut = new ArrayList<LocalDate>();

        while (isReserving){

            isChoosingHotel = true;
            isPerformingBookReservation = false;
            isChoosingRoom = false;
            isInputtingName = false;

            while (isChoosingHotel) {
                view.clearScreen();
                view.displayBookReservationPrompt(SB_HOTEL_SELECTION);
                if (!view.confirmUserAction("booking a reservation the hotel?")){
                    return;
                }
                validHotel = selectValidHotel();
                if (validHotel != null) {
                    isChoosingHotel = false;
                    isPerformingBookReservation = true;
                }

            }

            while (isPerformingBookReservation) {
                view.clearScreen();
                view.displayBookReservationPrompt(SB_OVERVIEW);
                if (!view.confirmUserAction("booking a reservation the hotel?")){
                    isPerformingBookReservation = false;
                } else {
                    view.clearScreen();

                    arrayLocalDatesCheckInCheckOut = selectValidCheckInAndCheckOutDates();

                    checkInDate = arrayLocalDatesCheckInCheckOut.get(0).getMonth() + "/" + arrayLocalDatesCheckInCheckOut.get(0).getDayOfMonth() + "/" + arrayLocalDatesCheckInCheckOut.get(0).getYear();
                    checkOutDate = arrayLocalDatesCheckInCheckOut.get(1).getMonth() + "/" + arrayLocalDatesCheckInCheckOut.get(1).getDayOfMonth() + "/" + arrayLocalDatesCheckInCheckOut.get(1).getYear();

                    if (view.confirmUserAction("\nuse the date On " + checkInDate + "\n" + checkOutDate + "?")) {
                        model.getNumOfAvailableRoomsByDate(validHotel, arrayLocalDatesCheckInCheckOut.getFirst());
                        listOfAvailableRooms = model.getAvailableRoomsByDate(validHotel, arrayLocalDatesCheckInCheckOut.getFirst());
                        isPerformingBookReservation = false;
                        isInputtingRoom = true;
                    }
                }
            }

            while (isInputtingRoom) {
                view.clearScreen();

                if (!view.confirmUserAction("booking a reservation the hotel?")){
                    isInputtingRoom = false;
                } else {
                    view.displayRoomList(validHotel, listOfAvailableRooms);
                    roomChoice = view.getInputInt("Input room number: ") - 1;
                    view.displayInvalidInputWarning((roomChoice < 0 || roomChoice >= listOfAvailableRooms.size()), "Room number not within index.");
                    if (roomChoice >= 0 && roomChoice < listOfAvailableRooms.size() ){
                        if (view.confirmUserAction("use room " + listOfAvailableRooms.get(roomChoice).getName() + "?")){
                            room = listOfAvailableRooms.get(roomChoice);
                            isInputtingRoom = false;
                            isInputtingName = true;
                        } else if (room == null) {
                            isInputtingRoom = false;
                            isInputtingName = false;
                            view.displayInvalidInputWarning();
                        }
                    }
                }
                view.pressEnterToContinue();
            }

            while (isInputtingName) {
                view.clearScreen();
                view.displayBookReservationPrompt(SB_GUEST_SELECTION);

                if (!view.confirmUserAction("booking a reservation the hotel?")){
                    isInputtingName = false;
                } else {
                    guestName = view.getInputStr("Please input your name:");
                    if (view.confirmUserAction("using the " + guestName)){
                        isChoosingRoom = true;
                        isInputtingName = false;
                    }


                }
            }

            while (isChoosingRoom) {
                view.clearScreen();
                view.displayBookReservationPrompt(SB_RESERVATION_CONFIRMATION);
                view.displayMessage("Hotel name: " + validHotel.getName() + "\tGuest name: " + guestName);
                view.displayMessage("CheckIn: " + arrayLocalDatesCheckInCheckOut.get(0).toString() + "\tCheck-out " + arrayLocalDatesCheckInCheckOut.get(1) );
                view.displayMessage("Room: " + room.getName() + " " + model.getRoomBasePricePerNight(validHotel, room) + " PHP per night");

                if (!view.confirmUserAction("booking a reservation the hotel?")){
                    isInputtingName = false;
                    isChoosingRoom = false;
                }

                if (isChoosingRoom && view.confirmUserAction("Confirm with these details?"))
                    model.makeReservation(validHotel.getName(), guestName, arrayLocalDatesCheckInCheckOut.get(0), arrayLocalDatesCheckInCheckOut.get(1), room);
                    isChoosingRoom = false;
                }
            }
        }

    /**
     * Starts the main program loop.
     * Displays the main action prompt and handles user input for various actions:
     * creating a hotel, viewing a hotel, managing a hotel, booking a reservation, and quitting the program.
     * Continues running until the user decides to quit.
     */
    public void start() {
        boolean isProgramRunning = true;

        /* program flow */
        while (isProgramRunning) {

            view.clearScreen();
            view.displayMainActionPrompt();

            char userInput;
            boolean isValidInput;
            do {
                userInput = view.getInputChar("\nEnter a response (C/V/B/M/Q): ");
                isValidInput = userInput == 'C' || userInput == 'V' || userInput == 'B' || userInput == 'M' || userInput == 'Q';
                view.displayInvalidInputWarning(isValidInput, "Please provide a valid response!");
            } while (!isValidInput);

            view.clearScreen();

            switch(userInput) {
                case View.CREATE_HOTEL_OPTION:
                    this.createHotel();
                    break;
                case View.VIEW_HOTEL_OPTION:
                    this.viewHotel();
                    break;
                case View.MANAGE_HOTEL_OPTION:
                    this.manageHotel();
                    break;
                case View.BOOK_RESERVATION_OPTION:
                    this.bookReservation();
                    break;
                case View.QUIT_OPTION:
                    isProgramRunning = false;
                    break;
            }

            view.clearScreen();
        }

        /* program termination sequence */
        view.displayProgramTerminationMessage();
    }
}