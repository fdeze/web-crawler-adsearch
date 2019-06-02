package fr.fabien.webcrawler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.fabien.contracts.adsearch.AdsearchOfferVo;
import fr.fabien.webcrawler.internal.AdsearchOfferService;

@EnableDiscoveryClient
@RestController
public class OfferController {
	private Logger logger = LoggerFactory.getLogger(OfferController.class);

	@Autowired
	private AdsearchOfferService adsearchProxy;

	@GetMapping(path = "/getOffers/adsearch")
	public List<AdsearchOfferVo> getOffers() {

		logger.info("Reception requête vers adsearch-microservice - getOffers");
		List<AdsearchOfferVo> lOfferList = adsearchProxy.getOffers();
		logger.info("Reception requête vers adsearch-microservice - getOffers - nombre résultats : {}",
				lOfferList.size());

		return lOfferList;

	}

}
