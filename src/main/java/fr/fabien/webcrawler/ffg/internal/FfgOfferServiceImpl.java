package fr.fabien.webcrawler.ffg.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.fabien.contracts.ffg.FfgOfferVo;
import fr.fabien.webcrawler.common.Constants;

@Service
public class FfgOfferServiceImpl implements FfgOfferService {

	private Logger logger = LoggerFactory.getLogger(FfgOfferServiceImpl.class);

	private static String URL = "http://www.fantasyflightgames.fr/prochainement";

	public List<FfgOfferVo> getOffers() {

		List<FfgOfferVo> lOfferList = new ArrayList<>();

		try {
			Document lDocument = Jsoup.connect(URL).userAgent(Constants.USER_AGENT).get();
			Elements articleElement = lDocument.select("#upcoming_container");
			Element article = articleElement.get(0).select(".grid_image_mode").get(0);

			articleElement = article.select(".product_sheet_main_container");
			logger.info("nombre résultats : {}", articleElement.size());
			FfgOfferVo offer;
			for (Element a : articleElement) {
				offer = new FfgOfferVo();
				offer.setTitre(a.select(".product_sheet_container_product_name a").text());
				offer.setDatePublication(a.select(".product_sheet_container_add_cart").text());

				Elements elements = a.select(".product_thumbnail img");
				offer.setUrlLogo(elements.attr("src"));
				lOfferList.add(offer);

			}
		} catch (IOException e) {
			logger.error("getOffers - error ", e);
		}

		return lOfferList;
	}

}