package com.udacity.vehicles.service;

import com.udacity.vehicles.VehiclesApiApplication;
import com.udacity.vehicles.client.maps.Address;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;

    private final PriceClient priceClient;
    private final MapsClient mapsClient;
    private final WebClient mapsClientAddress;


    public CarService(CarRepository repository, PriceClient priceClient, MapsClient mapsClient, @Qualifier("maps") WebClient mapsClientAddress) {
        /**
         * TODO: Add the Maps and Pricing Web Clients you create
         *   in `VehiclesApiApplication` as arguments and set them here.
         */
        this.priceClient = priceClient;
        this.mapsClient = mapsClient;
        this.repository = repository;
        this.mapsClientAddress = mapsClientAddress;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        List<Car> carList = repository.findAll();

        //Getting and setting price and address details of each car in a list
        List<Car> newCarList = carList
                .stream()
                .map(car -> {
                    car.setPrice(priceClient.getPrice(car.getId()));
                    Location location = car.getLocation();
                    Location foundLocation = mapsClient.getAddress(location);
                    car.setLocation(foundLocation);

                    Address address = mapsClientAddress.method(HttpMethod.GET) //.get()
                            .uri("http://localhost:9191/maps?lat="+car.getLocation().getLat() + "&lon="+ car.getLocation().getLon())
                            .retrieve()
                            .bodyToMono(Address.class)
                            .block();
                    assert address != null;

                    car.getLocation().setAddress(address.getAddress());
                    car.getLocation().setCity(address.getCity());
                    car.getLocation().setState(address.getState());
                    car.getLocation().setZip(address.getZip());

                    return car;
                })
                .collect(Collectors.toList());

        return newCarList;
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        Car foundCar = new Car();
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         *   Remove the below code as part of your implementation.
         */
        Optional<Car> car = Optional.of(repository.findById(id)).orElseThrow(CarNotFoundException::new);
        if(car.isPresent()){
            foundCar = car.get();
        }
        /**
         * TODO: Use the Pricing Web client you create in `VehiclesApiApplication`
         *   to get the price based on the `id` input'
         * TODO: Set the price of the car
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */
        String price = priceClient.getPrice(id);
        foundCar.setPrice(price);

        /**
         * TODO: Use the Maps Web client you create in `VehiclesApiApplication`
         *   to get the address for the vehicle. You should access the location
         *   from the car object and feed it to the Maps service.
         * TODO: Set the location of the vehicle, including the address information
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         *
         */

        Location location = foundCar.getLocation();
        Location foundLocation = mapsClient.getAddress(location);

        foundCar.setLocation(foundLocation);

        Address address = mapsClientAddress.method(HttpMethod.GET) //.get()
                .uri("http://localhost:9191/maps?lat="+foundCar.getLocation().getLat() + "&lon="+ foundCar.getLocation().getLon())
                .retrieve()
                .bodyToMono(Address.class)
                .block();
        assert address != null;

        foundCar.getLocation().setAddress(address.getAddress());
        foundCar.getLocation().setCity(address.getCity());
        foundCar.getLocation().setState(address.getState());
        foundCar.getLocation().setZip(address.getZip());

        return foundCar;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        carToBeUpdated.setCondition(car.getCondition());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         */
        Car found = new Car();
        Optional<Car> car = Optional.of(repository.findById(id)).orElseThrow(CarNotFoundException::new);

        /**
         * TODO: Delete the car from the repository.
         */
        if(car.isPresent()){
            found = car.get();
            repository.delete(found);
        }else{
            throw new CarNotFoundException();
        }

    }
}
