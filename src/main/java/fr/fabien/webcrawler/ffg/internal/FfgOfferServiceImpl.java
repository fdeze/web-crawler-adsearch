package fr.fabien.webcrawler.ffg.internal;

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
			logger.info("" + articleElement.size());
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

	/**
	 * Get offers presents into the current page
	 * 
	 * @param pPageNumber
	 * @return @List<ffgOfferVo>
	 */
	private List<FfgOfferVo> getOffers(Integer pPageNumber) {
		List<FfgOfferVo> lOfferList = new ArrayList<>();
		try {
			String lUrl = URL + "&paged=" + pPageNumber;

			Document lDocument = Jsoup.connect(lUrl).userAgent(Constants.USER_AGENT).get();

			FfgOfferVo lOffer;
			Elements lAElement;
			Elements lMetaElements;
			Elements lDateElements;
			Elements lDescriptionOffreElements;
			Elements articleElements = lDocument.select(".list-offers article");
			String lurl;
			for (Element article : articleElements) {

				lAElement = article.select(".job__wrap .job__main a");
				lOffer = new FfgOfferVo();
				lurl = lAElement.attr("href");
				lOffer.setNumeroOffreExterne("ffg_" + lurl.hashCode());
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
						lOffer.setNumeroOffreExterne("ffg_" + valeurMeta);
					}

				}

				lDescriptionOffreElements = lDocumentOffre.select(".page__content__main__desc__inner");
				String description = lDescriptionOffreElements.text();

				String[] descriptionArray = description.split("Profil recherché");
				lOffer.setDescriptionOffre(descriptionArray[0]);
				lOffer.setDescriptionProfil(descriptionArray[1]);

				lOfferList.add(lOffer);
			}
			return lOfferList;
		} catch (IOException e) {
			logger.error("getOffers - error ", e);
			return lOfferList;
		}

	}

}