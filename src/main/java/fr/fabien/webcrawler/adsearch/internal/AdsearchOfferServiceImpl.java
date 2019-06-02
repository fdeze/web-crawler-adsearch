package fr.fabien.webcrawler.adsearch.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import fr.fabien.contracts.adsearch.AdsearchOfferVo;
import fr.fabien.webcrawler.common.Constants;

@Service
public class AdsearchOfferServiceImpl implements AdsearchOfferService {

	private Logger logger = LoggerFactory.getLogger(AdsearchOfferServiceImpl.class);

	private static String URL = "https://adsearch.fr/nos-offres/?locations[]=69&subsidiaries[]=5&contracts[]=2";

	public List<AdsearchOfferVo> getOffers() {

		List<AdsearchOfferVo> lOfferList = new ArrayList<>();
		List<Integer> listeCompteur = new ArrayList<>();

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
		List<AdsearchOfferVo> lOfferList = new ArrayList<>();
		try {
			String lUrl = URL + "&paged=" + pPageNumber;

			Document lDocument = Jsoup.connect(lUrl).userAgent(Constants.USER_AGENT).get();

			AdsearchOfferVo lOffer;
			Elements lAElement;
			Elements lMetaElements;
			Elements lDateElements;
			Elements lDescriptionOffreElements;
			Elements articleElements = lDocument.select(".list-offers article");
			String lurl;
			for (Element article : articleElements) {

				lAElement = article.select(".job__wrap .job__main a");
				lOffer = new AdsearchOfferVo();
				lurl = lAElement.attr("href");
				lOffer.setNumeroOffreExterne("ADSEARCH_" + lurl.hashCode());
				lOffer.setNumeroOffre(String.valueOf(lurl.hashCode()));
				lOffer.setUrl(lurl);
				lOffer.setTitre(lAElement.select(".job__main__title").text());

				lMetaElements = article.select(".job__wrap .job__metas span");
				String metaText;
				for (Element meta : lMetaElements) {
					metaText = meta.select("span").text();
					if (!"CDI".equals(metaText)) {
						if (metaText.startsWith("Entre")) {
							lOffer.setSalaire(metaText);
						} else {
							lOffer.setAdresse(metaText);
						}
					}
				}

				lDateElements = article.select(".job__footer .job__footer__date");

				String datePublication = lDateElements.eachText().toString();
				datePublication = datePublication.replaceAll("\\[Mise en ligne le ", "").replaceAll("]", "");
				lOffer.setDatePublication(datePublication);
				lOffer.setPage(pPageNumber);

				Document lDocumentOffre = Jsoup.connect(lurl).userAgent(Constants.USER_AGENT).get();
				lMetaElements = lDocumentOffre.select(".page__content__main__side__metas__item");
				for (Element element : lMetaElements) {

					Elements labelsElements = element.select(".page__content__main__side__metas__item__label");
					String libelleMeta = labelsElements.text().trim();
					String valeurMeta = element.select("span").get(1).html();

					if ("Référence de l'offre".equals(libelleMeta)) {
						lOffer.setNumeroOffre(valeurMeta);
						lOffer.setNumeroOffreExterne("ADSEARCH_" + valeurMeta);
					}

				}

				lDescriptionOffreElements = lDocumentOffre.select(".page__content__main__desc__inner");	
				String description = lDescriptionOffreElements.text();						
				lOffer.setDescriptionOffre(description.split("Profil recherché")[0]);
				lOffer.setDescriptionProfil(description.split("Profil recherché")[1]);

				lOfferList.add(lOffer);
			}
			return lOfferList;
		} catch (IOException e) {
			logger.error("getOffers - error ", e);
			return lOfferList;
		}

	}

}