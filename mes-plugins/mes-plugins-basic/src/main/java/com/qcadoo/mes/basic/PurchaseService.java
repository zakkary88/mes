package com.qcadoo.mes.basic;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.basic.constants.BasicConstants;
import com.qcadoo.mes.basic.constants.PurchaseFields;
import com.qcadoo.mes.basic.util.NumberDisplayService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchCriterion;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.ComponentState.MessageType;
import com.qcadoo.view.api.components.GridComponent;


@Service
public class PurchaseService {
		
	@Autowired
	private DataDefinitionService dataDefinitionService;
	
	@Autowired
	private NumberDisplayService numberDisplayService;

	public boolean checkIfThereIsNoPurchaseWithTheSameProductAndPrice(final DataDefinition dataDefinition, final Entity purchase){
		BigDecimal currentPrice = (BigDecimal) purchase.getField(PurchaseFields.PRICE);
		Entity product = (Entity) purchase.getField(PurchaseFields.PRODUCT);
		
		SearchCriterion priceCriterion = SearchRestrictions.eq(PurchaseFields.PRICE, currentPrice);
		SearchCriterion productCriterion = SearchRestrictions.belongsTo(PurchaseFields.PRODUCT, product);
		Entity existingPurchase = preparePurchaseCriteriaBuilder(priceCriterion, productCriterion).setMaxResults(1).uniqueResult();
	
		if (existingPurchase != null) {
			purchase.addError(dataDefinition.getField(PurchaseFields.PRODUCT), "basic.purchase.already.existing.purchase.error");
            return false;
        }
		
		return true;
	}
	
	public final void getAveragePurchasesPrice(final ViewDefinitionState view, final ComponentState state, final String[] args){
		final GridComponent purchasesGrid = (GridComponent) view.getComponentByReference(PurchaseFields.PURCHASES_GRID);
		if(purchasesGrid.getEntities().isEmpty()){
			return;
		}
		
		Double averagePrice = getAveragePrice();
		String displayablePrice = numberDisplayService.formatValue(averagePrice);
		purchasesGrid.addMessage("basic.purchasesList.message.getAveragePurchasesPrice", MessageType.INFO, displayablePrice);
	}
	
	public final void displayProductUnit(final ViewDefinitionState view, final ComponentState state, final String[] args){
		displayProductUnit(view);
	}
	
	public final void displayProductUnit(final ViewDefinitionState view){
		ComponentState productComponent = (ComponentState) view.getComponentByReference(PurchaseFields.PRODUCT);
		Long productId = (Long) productComponent.getFieldValue();
				
		SearchCriterion idCriterion = SearchRestrictions.idEq(productId);
		Entity product = prepareProductCriteriaBuilder(idCriterion).setMaxResults(1).uniqueResult();
		String unit = (String) product.getField(PurchaseFields.UNIT);
		
		ComponentState unitComponent = (ComponentState) view.getComponentByReference(PurchaseFields.UNIT);
		unitComponent.setFieldValue(unit);
	}
	
	private SearchCriteriaBuilder prepareProductCriteriaBuilder(final SearchCriterion... criterions) {
		SearchCriteriaBuilder scb = getProductDataDefinition().find();
		for(SearchCriterion c : criterions){
			if (c != null) {
				scb.add(c);
			}
		}
		return scb;
	}
	
	private SearchCriteriaBuilder preparePurchaseCriteriaBuilder(final SearchCriterion... criterions) {
		SearchCriteriaBuilder scb = getPurchaseDataDefinition().find();
		for(SearchCriterion c : criterions){
			if (c != null) {
				scb.add(c);
			}
		}
		return scb;
	}
	
	private Double getAveragePrice(){
		Double averagePrice = 0.0;
		SearchResult result = getPurchaseDataDefinition().find().list();
		List<Entity> allPurchases = result.getEntities();
		if(!allPurchases.isEmpty()){
			for(Entity entity : allPurchases){
				BigDecimal purchasePrice = (BigDecimal) entity.getField(PurchaseFields.PRICE);
				averagePrice += purchasePrice.doubleValue();
			}
			averagePrice /= allPurchases.size();
		}
		return averagePrice; 
	}
	
	private DataDefinition getPurchaseDataDefinition() {
		return dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PURCHASE);
	}
	
	private DataDefinition getProductDataDefinition() {
		return dataDefinitionService.get(BasicConstants.PLUGIN_IDENTIFIER, BasicConstants.MODEL_PRODUCT);
	}
}
