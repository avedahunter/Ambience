package edu.rit.csci759.rspi;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import edu.rit.csci759.rspi.utils.MCP3008ADCReader;

public class RpiIndicatorImpl implements RpiIndicatorInterface{
	
	private final GpioController gpio = GpioFactory.getInstance();

	private final GpioPinDigitalOutput pin00 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "Green", PinState.LOW);
	private final GpioPinDigitalOutput pin01 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "Yellow", PinState.LOW);;
	private final GpioPinDigitalOutput pin02 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "Red", PinState.LOW);;
	
	public RpiIndicatorImpl(){
		led_all_off();
		MCP3008ADCReader.initSPI(gpio);			
	}
	
	public void GPIOshutDown(){
		gpio.shutdown();
	}
	
	@Override
	public void led_all_off() {
		// TODO Auto-generated method stub
		pin00.low();
        System.out.println("--> Green LED should be: OFF");
        
        pin01.low();
        System.out.println("--> Yellow LED should be: OFF");
        
        pin02.low();
        System.out.println("--> Red LED should be: OFF");
	}

	@Override
	public void led_all_on() {	
		pin00.high();
        System.out.println("--> Green LED should be: ON");
        
        pin01.high();
        System.out.println("--> Yellow LED should be: ON");
        
        pin02.high();
        System.out.println("--> Red LED should be: ON");
	}

	@Override
	public void led_error(int blink_count) throws InterruptedException {
		
		System.out.println("Error: Red LED pulsing.");
		
		for(int iCounter = 0; iCounter< blink_count; ++iCounter){
			pin02.toggle();
			Thread.sleep(500);
		}
		
		pin02.low();

	}

	@Override
	public void led_when_low() {
		
        pin00.high();	//Green LED to indicate that the blind is at the highest position 
        System.out.println("--> Green LED should be: ON");
	}

	@Override
	public void led_when_mid() {
		 
        pin01.high();	//Yellow LED to indicate that the blind is at the middle position 
        System.out.println("--> Yellow LED should be: ON");
	}

	@Override
	public void led_when_high() {
		
        pin02.high();	//Red LED to indicate that the blind is at the highest position 
        System.out.println("--> Red LED should be: ON");
	}

	@Override
	public int  read_ambient_light_intensity() {
		
		/*
		 * Reading ambient light from the photocell sensor using the MCP3008 ADC 
		 */
		int adc_ambient = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH1.ch());
		// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
		// convert in the range of 1-100
		int ambient = (int)(adc_ambient / 10.24); 

		return ambient;
	}
	
	@Override
	public int read_temperature() {
		
		/*
		 * Reading temperature from the TMP36 sensor using the MCP3008 ADC 
		 */
		int adc_temperature = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH0.ch());
		// [0, 1023] ~ [0x0000, 0x03FF] ~ [0&0, 0&1111111111]
		// convert in the range of 1-100
		int temperature = (int)(adc_temperature / 10.24);
		
		return temperature;
	}

}
