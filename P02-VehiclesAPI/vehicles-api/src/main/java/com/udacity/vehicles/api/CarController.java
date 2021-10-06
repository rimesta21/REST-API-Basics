package com.udacity.vehicles.api;



import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resources;
import javax.validation.Valid;


import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Implements a REST-based controller for the Vehicles API.
 * HATEOAS Implementation from: https://www.baeldung.com/spring-hateoas-tutorial
 */
@RestController
@RequestMapping("/cars")
class CarController {

    private final CarService carService;

    CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Creates a list to store any vehicles.
     * @return list of vehicles
     */
    @GetMapping
    public CollectionModel<Car> list() {
        List<Car> cars = carService.list();
        for(Car car : cars) {
            Long carId = car.getId();
            car = carService.findById(carId);
            Link selfLink = linkTo(CarController.class).slash(carId).withSelfRel();
            car.add(selfLink);
        }
        Link link = linkTo(CarController.class).withSelfRel();
        return CollectionModel.of(cars, link);
    }

    /**
     * Gets information of a specific car by ID.
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @GetMapping("/{id}")
    ResponseEntity<Car> get(@PathVariable Long id) {
        /**
         * TODO: Use the `findById` method from the Car Service to get car information.
         * TODO: Use the `assembler` on that car and return the resulting output.
         *   Update the first line as part of the above implementing.
         */
        Car car = carService.findById(id);
        return getCarResponseEntity(id, car);
    }

    private ResponseEntity<Car> getCarResponseEntity(Long id, Car car) {
        Link selfLink = linkTo(CarController.class).slash(id).withRel("carById");
        Link carsLink = linkTo(methodOn(CarController.class).list()).withRel("allCars");
        car.add(selfLink);
        car.add(carsLink);
        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    /**
     * Posts information to create a new vehicle in the system.
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @PostMapping
    ResponseEntity<Car> post(@Valid @RequestBody Car car) throws URISyntaxException {
        //@Valid - the fields in the car request body need to be filled out
        /**
         * TODO: Use the `save` method from the Car Service to save the input car.
         * TODO: Use the `assembler` on that saved car and return as part of the response.
         *   Update the first line as part of the above implementing.
         */
        carService.save(car);
        car = carService.findById(car.getId());
        Long carId = car.getId();
        Link idLink = linkTo(CarController.class).slash(carId).withRel("carById");
        Link carsLink = linkTo(methodOn(CarController.class).list()).withRel("allCars");
        Link selfLink = linkTo(CarController.class).withSelfRel();
        car.add(idLink);
        car.add(carsLink);
        car.add(selfLink);
        return new ResponseEntity<>(car, HttpStatus.CREATED);
    }

    /**
     * Updates the information of a vehicle in the system.
     * @param id The ID number for which to update vehicle information.
     * @param car The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @PutMapping("/{id}")
    ResponseEntity<Car> put(@PathVariable Long id, @Valid @RequestBody Car car) {
        /**
         * TODO: Set the id of the input car object to the `id` input.
         * TODO: Save the car using the `save` method from the Car service
         * TODO: Use the `assembler` on that updated car and return as part of the response.
         *   Update the first line as part of the above implementing.
         */
        car.setId(id);
        carService.save(car);
        car = carService.findById(id);
        return getCarResponseEntity(id, car);
    }

    /**
     * Removes a vehicle from the system.
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @DeleteMapping("/{id}")
    ResponseEntity<Link> delete(@PathVariable Long id) {
        /**
         * TODO: Use the Car Service to delete the requested vehicle.
         */
        carService.delete(id);
        return new ResponseEntity<>(linkTo(methodOn(CarController.class).list()).withRel("allCars"),HttpStatus.OK);
    }
}
