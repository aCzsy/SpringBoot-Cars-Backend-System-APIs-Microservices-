package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import org.assertj.core.api.Assertions;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {

	private final int port = 8082;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void showAllPrices(){
		ResponseEntity<Resources> responseEntity
				= this.restTemplate.getForEntity("http://localhost:" + port +  "/prices", Resources.class);

		assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
	}

	@Test
	public void getPrice(){
		final int priceId = 3;
		ResponseEntity<Resource> responseEntity
				= this.restTemplate.getForEntity("http://localhost:" + port + "/prices/" + priceId, Resource.class);

		assertThat(responseEntity.getStatusCode(), equalTo(HttpStatus.OK));
	}

}
