package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;

    @Autowired
    TrainService trainService;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db

        /*   Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get();
        String s = train.getRoute();
        int count =0, startStationIndex = 0, endStationIndex = 0;
        String[] list = s.split(",");
        for (int i=0;i<list.length;i++) {
            if (list[i] == String.valueOf(bookTicketEntryDto.getFromStation())) {
                count++;
                startStationIndex = i+1;
            }
            if (list[i] == String.valueOf(bookTicketEntryDto.getToStation())) {
                count++;
                endStationIndex=i+1;
            }
        }
        if(count!=2)
            throw new Exception("Invalid stations");
        else {

            SeatAvailabilityEntryDto isAvailable = new SeatAvailabilityEntryDto();
            isAvailable.setTrainId(bookTicketEntryDto.getTrainId());
            isAvailable.setFromStation(bookTicketEntryDto.getFromStation());
            isAvailable.setToStation(bookTicketEntryDto.getToStation());

            int availableSeats = trainService.calculateAvailableSeats(isAvailable);
            if (bookTicketEntryDto.getNoOfSeats() > availableSeats)
                throw new Exception("Less tickets are available");
            else {

                List<Integer> passengerIds = bookTicketEntryDto.getPassengerIds();
                List<Passenger> passengers = new ArrayList<>();

                Ticket ticket = new Ticket();

                for(int id:passengerIds){
                    Passenger passenger = passengerRepository.findById(id).get();
                    // passenger.get
                    passengers.add(passenger);
                }

                int fare = (endStationIndex - startStationIndex) * 300 * passengers.size();

                ticket.setFromStation(bookTicketEntryDto.getFromStation());
                ticket.setToStation(bookTicketEntryDto.getToStation());
                ticket.setPassengersList(passengers);
                ticket.setTotalFare(fare);
                ticket.setTrain(train);

                return ticketRepository.save(ticket).getTicketId();
            }
        }*/

        Train train=trainRepository.findById(bookTicketEntryDto.getTrainId()).get();
        int bookedSeats=0;
        List<Ticket>booked=train.getBookedTickets();
        for(Ticket ticket:booked){
            bookedSeats+=ticket.getPassengersList().size();
        }

        if(bookedSeats+bookTicketEntryDto.getNoOfSeats()> train.getNoOfSeats()){
            throw new Exception("Less tickets are available");
        }

        String stations[]=train.getRoute().split(",");
        List<Passenger>passengerList=new ArrayList<>();
        List<Integer>ids=bookTicketEntryDto.getPassengerIds();
        for(int id: ids){
            passengerList.add(passengerRepository.findById(id).get());
        }
        int x=-1,y=-1;
        for(int i=0;i<stations.length;i++){
            if(bookTicketEntryDto.getFromStation().toString().equals(stations[i])){
                x=i;
                break;
            }
        }
        for(int i=0;i<stations.length;i++){
            if(bookTicketEntryDto.getToStation().toString().equals(stations[i])){
                y=i;
                break;
            }
        }
        if(x==-1||y==-1||y-x<0){
            throw new Exception("Invalid stations");
        }
        Ticket ticket=new Ticket();
        ticket.setPassengersList(passengerList);
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());

        int fair=0;
        fair=bookTicketEntryDto.getNoOfSeats()*(y-x)*300;

        ticket.setTotalFare(fair);
        ticket.setTrain(train);

        train.getBookedTickets().add(ticket);
        train.setNoOfSeats(train.getNoOfSeats()-bookTicketEntryDto.getNoOfSeats());

        Passenger passenger=passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
        passenger.getBookedTickets().add(ticket);

        trainRepository.save(train);

        return ticketRepository.save(ticket).getTicketId();

    }
}
