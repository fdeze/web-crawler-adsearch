package fr.fabien.webcrawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.fabien.webcrawler.common.Constants;
import fr.fabien.webcrawler.common.model.AdsearchOfferVo;

@EnableDiscoveryClient
@RestController
public class OfferController {

	private static String URL = "https://adsearch.fr/nos-offres/?locations[]=69&subsidiaries[]=5&contracts[]=2";

	@GetMapping(path = "/getOffers/adsearch")
	public List<AdsearchOfferVo> getOffers() {

		List<AdsearchOfferVo> lOfferList = new ArrayList();
		List<Integer> listeCompteur = new ArrayList();

		int lOfferNumber = getOfferNumber();
		double lPageNumber = Math.ceil(lOfferNumber / 10.0) + 1;

		IntStream.rangeClosed(1, (int) lPageNumber);

		for (int i = 1; i <= lPageNumber; i++) {
			listeCompteur.add(i);
		}

		listeCompteur.parallelStream().forEach(numPage -> lOfferList.addAll(getOffers(numPage)));

		// sorting
		lOfferList.sort(Comparator.comparing(AdsearchOfferVo::getPage));
		return lOfferList;

	}

	private int getOfferNumber() {
		try {
			Document lDocument = Jsoup.connect(URL).userAgent(Constants.USER_AGENT).get();
			Elements lOfferElements = lDocument.select(".page__content__total");
			return Integer.parseInt(lOfferElements.eachText().get(0).substring(0, 2));

		} catch (IOException e) {
			return 0;
		}
	}

	private List<AdsearchOfferVo> getOffers(Integer pPageNumber) {
		List<AdsearchOfferVo> lOfferList = new ArrayList();
		try {
			String lUrl = URL + "&paged=" + pPageNumber;

			Document lDocument = Jsoup.connect(lUrl).userAgent(Constants.USER_AGENT).get();

			AdsearchOfferVo lOffer;
			Elements lAElement;
			Elements lMetaElements;
			Elements lDateElements;
			Elements articleElements = lDocument.select(".list-offers article");
			String lurl;
			for (Element article : articleElements) {

				lAElement = article.select(".job__wrap .job__main a");
				lOffer = new AdsearchOfferVo();
				lurl = lAElement.attr("href");
				lOffer.setNumeroOffreExterne("ADSEARCH_" + lurl.hashCode());
				lOffer.setUrl(lurl);
				lOffer.setTitre(lAElement.select(".job__main__title").text());

				lMetaElements = article.select(".job__wrap .job__metas span");
				for (Element meta : lMetaElements) {
					lOffer.getMetas().add(meta.select("span").text());
				}

				lDateElements = article.select(".job__footer .job__footer__date");
				lOffer.setDatePublication(lDateElements.eachText().toString());

				lOffer.setPage(pPageNumber);

				lOfferList.add(lOffer);
			}
			return lOfferList;
		} catch (IOException e) {
			return lOfferList;
		}

	}

}
