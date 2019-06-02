package fr.fabien.webcrawler.internal;

import java.util.List;

import fr.fabien.contracts.adsearch.AdsearchOfferVo;

public interface AdsearchOfferService {

	public List<AdsearchOfferVo> getOffers();
}
