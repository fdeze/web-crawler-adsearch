package fr.fabien.webcrawler.internal;

import java.util.ArrayList;
import java.util.List;

import fr.fabien.webcrawler.common.model.AbstractOfferVo;

public class AdsearchOfferVo extends AbstractOfferVo {

	private List<String> metas = new ArrayList<>();

	private Integer page;

	public List<String> getMetas() {
		return metas;
	}

	public Integer getPage() {
		return page;
	}

	public void setMetas(List<String> metas) {
		this.metas = metas;
	}

	public void setPage(Integer numPage) {
		this.page = numPage;
	}

	@Override
	public String toString() {
		return "Emploi [titre=" + titre + ", url=" + url + ", metas=" + metas + ", datePublication=" + datePublication
				+ ", page=" + page + "]";
	}

}
