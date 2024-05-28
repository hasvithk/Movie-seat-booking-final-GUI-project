
package registration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieTheaterBooking {

    public static void main(String[] args) {
        new TheaterFrame();
    }
}

class TheaterFrame extends JFrame implements ActionListener {
    private final Container c;
    private final JLabel title;
    private final JComboBox<String> movieList;
    private final JButton[][] seats;
    private final JTextArea bookingDetails;
    private final JButton book;
    private final List<String> selectedSeats;
    private final Set<String> bookedSeats;
    private static final int NUM_ROWS = 11;
    private static final int SEATS_PER_ROW = 5;
    private static final double TICKET_PRICE = 10.0;
    private final String[] movies = {"Avengers", "Inception", "Interstellar"};
    private static final String BOOKED_SEATS_FILE = "booked_seats.txt";

    public TheaterFrame() {
        setTitle("Movie Theater Seat Booking System");
        setBounds(100, 100, 800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        title = new JLabel("Movie Theater Booking");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setSize(300, 30);
        title.setLocation(250, 30);
        c.add(title);

        movieList = new JComboBox<>(movies);
        movieList.setFont(new Font("Arial", Font.PLAIN, 16));
        movieList.setSize(200, 30);
        movieList.setLocation(50, 80);
        c.add(movieList);

        seats = new JButton[NUM_ROWS][SEATS_PER_ROW];
        selectedSeats = new ArrayList<>();
        bookedSeats = new HashSet<>();
        loadBookedSeats();

        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < SEATS_PER_ROW; j++) {
                seats[i][j] = new JButton();
                seats[i][j].setBounds(50 + j * 90, 150 + i * 50, 80, 40);
                seats[i][j].setFont(new Font("Arial", Font.BOLD, 16));
                String seatId = "Row " + (i + 1) + " Seat " + (j + 1);
                seats[i][j].setText(seatId);
                if (bookedSeats.contains(seatId)) {
                    seats[i][j].setBackground(Color.RED);
                    seats[i][j].setEnabled(false);
                } else {
                    seats[i][j].addActionListener(this);
                }
                c.add(seats[i][j]);
            }
        }

        bookingDetails = new JTextArea();
        bookingDetails.setFont(new Font("Arial", Font.PLAIN, 15));
        bookingDetails.setSize(300, 400);
        bookingDetails.setLocation(600, 400);
        bookingDetails.setLineWrap(true);
        bookingDetails.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(bookingDetails);
        scrollPane.setSize(300, 400);
        scrollPane.setLocation(470, 100);
        c.add(scrollPane);

        book = new JButton("Book Seats");
        book.setFont(new Font("Arial", Font.PLAIN, 18));
        book.setSize(200, 40);
        book.setLocation(300, 700);
        book.addActionListener(this);
        c.add(book);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < SEATS_PER_ROW; j++) {
                if (e.getSource() == seats[i][j]) {
                    String seatId = "Row " + (i + 1) + " Seat " + (j + 1);
                    if (selectedSeats.contains(seatId)) {
                        seats[i][j].setBackground(null);
                        selectedSeats.remove(seatId);
                    } else {
                        seats[i][j].setBackground(Color.GREEN);
                        selectedSeats.add(seatId);
                    }
                    updateBookingDetails();
                    return;
                }
            }
        }
        if (e.getSource() == book) {
            if (selectedSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No seats selected!");
            } else {
                bookedSeats.addAll(selectedSeats);
                saveBookedSeats();
                JOptionPane.showMessageDialog(this, "Seats booked successfully! Total Price: $" + (selectedSeats.size() * TICKET_PRICE));
                resetBooking();
            }
        }
    }

    private void updateBookingDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Selected Movie: ").append(movieList.getSelectedItem()).append("\n");
        details.append("Selected Seats:\n");
        selectedSeats.forEach(seat -> details.append(seat).append("\n"));
        details.append("Total Price: $").append(selectedSeats.size() * TICKET_PRICE).append("\n");
        bookingDetails.setText(details.toString());
    }

    private void resetBooking() {
        selectedSeats.clear();
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < SEATS_PER_ROW; j++) {
                if (!bookedSeats.contains("Row " + (i + 1) + " Seat " + (j + 1))) {
                    seats[i][j].setBackground(null);
                }
            }
        }
        updateBookingDetails();
    }

    private void saveBookedSeats() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKED_SEATS_FILE))) {
            for (String seat : bookedSeats) {
                writer.write(seat);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBookedSeats() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKED_SEATS_FILE))) {
            String seat;
            while ((seat = reader.readLine()) != null) {
                bookedSeats.add(seat);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
