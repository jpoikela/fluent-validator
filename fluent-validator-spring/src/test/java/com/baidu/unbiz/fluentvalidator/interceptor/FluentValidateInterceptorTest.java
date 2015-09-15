package com.baidu.unbiz.fluentvalidator.interceptor;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.baidu.unbiz.fluentvalidator.dto.Car;
import com.baidu.unbiz.fluentvalidator.error.CarError;
import com.baidu.unbiz.fluentvalidator.exception.CarException;
import com.baidu.unbiz.fluentvalidator.service.CarService;
import com.google.common.collect.Lists;

/**
 * @author zhangxu
 */
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
public class FluentValidateInterceptorTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private CarService carService;

    @Test
    public void testAddCar() {
        Car car = getValidCar();
        carService.addCar(car);
    }

    @Test
    public void testAddCar2() {
        Car car = getValidCar();
        carService.addCar(1, car);
    }

    @Test
    public void testAddCar3() {
        Car car = getValidCar();
        carService.addCar("abc", 9L, car);
    }

    @Test
    public void testAddCarList() {
        List<Car> cars = getValidCars();
        carService.addCars("abc", cars);
    }

    @Test
    public void testAddCarListNull() {
        List<Car> cars = null;
        carService.addCars("abc", cars);
    }

    @Test
    public void testAddCarArray() {
        Car[] cars = getValidCars().toArray(new Car[] {});
        carService.addCars(cars, 9d);
    }

    @Test
    public void testAddCarArrayNull() {
        Car[] cars = null;
        carService.addCars(cars, 9d);
    }

    @Test
    public void testAddCarNegative() {
        try {
            Car car = getValidCar();
            car.setLicensePlate("BEIJING123");
            carService.addCar(car);
        } catch (CarException e) {
            System.out.println(e.getMessage());
            assertThat(e.getMessage(), Matchers.is("License is not valid, invalid value=BEIJING123"));
            return;
        }
        fail();
    }

    @Test
    public void testAddCarNegative2() {
        try {
            Car car = getValidCar();
            car.setLicensePlate("XXXX");
            carService.addCar(car);
        } catch (CarException e) {
            System.out.println(e.getMessage());
            assertThat(e.getCause().getMessage(), Matchers.is("Call Rpc failed"));
            return;
        }
        fail();
    }

    @Test
    public void testAddCarJsr303() {
        try {
            Car car = getValidCar();
            car.setManufacturer("");
            carService.addCar(car);
        } catch (CarException e) {
            System.out.println(e.getMessage());
            assertThat(e.getMessage().contains("{manufacturer}"), Matchers.is(true));
            return;
        }
        fail();
    }

    @Test
    public void testAddCarNull() {
        try {
            List<Car> cars = getValidCars();
            carService.addCarsWithAddOnChecks("abc", cars);

            cars = null;
            cars = carService.addCarsWithAddOnChecks("abc", cars);
        } catch (CarException e) {
            System.out.println(e.getMessage());
            assertThat(e.getMessage(), Matchers.is(CarError.CAR_NULL.msg()));
            return;
        }
        fail();
    }

    @Test
    public void testAddCarSizeExceed() {
        try {
            List<Car> cars = getValidCars();
            carService.addCarsWithAddOnChecks("abc", cars);

            cars = getManyValidCars();
            carService.addCarsWithAddOnChecks("abc", cars);
        } catch (CarException e) {
            System.out.println(e.getMessage());
            assertThat(e.getMessage(), Matchers.is(CarError.CAR_SIZE_EXCEED.msg()));
            return;
        }
        fail();
    }

    private Car getValidCar() {
        return new Car("BMW", "LA1234", 5);
    }

    private List<Car> getValidCars() {
        return Lists.newArrayList(new Car("BMW", "LA1234", 5),
                new Car("Benz", "NYCuuu", 2),
                new Car("Chevrolet", "LA1234", 7));
    }

    private List<Car> getManyValidCars() {
        List<Car> ret = Lists.newArrayList();
        for (int i = 0; i < 50; i++) {
            ret.add(new Car("BMW", "LA1234", 5));
        }
        return ret;
    }

}