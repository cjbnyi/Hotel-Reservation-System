import java.time.LocalDate;
import java.util.ArrayList;

public class Model {

    private ArrayList<Hotel> hotelList;

    public Model() {
        this.hotelList = new ArrayList<Hotel>();
    }

    public Hotel addHotel(String name) {
        for (Hotel hotel : hotelList)
            if (hotel.getName().equals(name))
                return null;
        Hotel hotel = new Hotel(name);
        this.hotelList.add(hotel);
        return hotel;
    }

    public double getHotelEstimatedEarnings(Hotel hotel) {
        double totalEarnings = 0.0;
        for (Reservation reservation : hotel.getReservationList())
            totalEarnings += reservation.getTotalPrice();
        return totalEarnings;
    }

    public int getTotalAvailableRoomsByDate(Hotel hotel, LocalDate date) { // not sure if we should do these
        return hotel.filterAvailableRoomsByDate(date).size();
    }

    public int getTotalBookedRoomsByDate(Hotel hotel, LocalDate date) {
        return hotel.getTotalRooms() - hotel.filterAvailableRoomsByDate(date).size();
    }


    /**
     * Sets the name of a hotel object given its current name.
     *
     * @param currentName - current name of the hotel being renamed
     * @param newName - new name of the hotel being renamed
     * @return 0 if no hotel with the old name exists, 1 if name is not unique, 2 if renaming was successful
     */
    public int setHotelName(String currentName, String newName) {
        boolean hotelExists = false;

        for (Hotel hotel : this.hotelList) {
            String hotelName = hotel.getName();
            if (hotelName.equals(currentName))
                hotelExists = true;
            if (hotelName.equals(newName))
                return 1;
        }

        if (!hotelExists)
            return 0;

        for (Hotel hotel : this.hotelList) {
            String hotelName = hotel.getName();
            if (hotelName.equals(currentName))
                hotel.setName(newName);
        }

        return 2;
    }


    /**
     * Gets the array of hotels.
     *
     * @return An array of hotels.
     */
    public ArrayList<Hotel> getHotelList() {
        return this.hotelList;
    }
}