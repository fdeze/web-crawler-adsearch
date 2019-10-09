package fr.fabien.webcrawler.ffg;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.fabien.contracts.ffg.FfgOfferVo;
import fr.fabien.webcrawler.ffg.internal.FfgOfferService;

@EnableDiscoveryClient
@RestController
public class OfferController {
	private Logger logger = LoggerFactory.getLogger(OfferController.class);

	@Autowired
	private FfgOfferService ffgProxy;

	@GetMapping(path = "/getOffers/ffg", produces = { "application/json" })
	public List<FfgOfferVo> getOffers() {

		logger.info("Reception requête vers ffg-microservice - getOffers");
		List<FfgOfferVo> lOfferList = ffgProxy.getOffers();
		logger.info("Reception requête vers ffg-microservice - getOffers - nombre résultats : {}",
				lOfferList.size());

		return lOfferList;

	}

}
