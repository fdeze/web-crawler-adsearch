package fr.fabien.webcrawler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WebCrawlerApplicationTests {

	MockMvc mockMvc;

	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	OfferController offerController;

	@Before
	public void setup() throws Exception {
		this.mockMvc = standaloneSetup(this.offerController).build();
	}

	@Test
	public void testGetOffers() throws Exception {
		mockMvc.perform(get("/getOffers/adsearch").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

}