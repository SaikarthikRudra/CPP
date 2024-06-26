package com.exavalu.customer.product.portal.entities.dto.product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.exavalu.customer.product.portal.entities.mongodb.Product;
import com.exavalu.customer.product.portal.entities.salesforce.ProductSF;

public class ProductMapper {
	public static ProductDtoParticularItem toDto(ProductSF product) {
		ProductDtoParticularItem dto = new ProductDtoParticularItem();
		dto.setBrandName(product.getBrandName());
		dto.setPrice(product.getPrice());
		dto.setProductTitle(product.getProductTitle());
		dto.setRam(product.getRam());
		dto.setRom(product.getRom());
		dto.setSeriesName(product.getSeriesName());
		dto.setWarranty(product.getWarranty());
		dto.setQuantity(product.getQuantity());
		dto.setLocation(product.getLocation());
		return dto;
	}

	public static ProductDtoAllItem toDtoAll(ProductSF product) {
		ProductDtoAllItem dto = new ProductDtoAllItem();
		dto.setBrandName(product.getBrandName());
		dto.setPrice(product.getPrice());
		dto.setProductTitle(product.getProductTitle());
		dto.setSeriesName(product.getSeriesName());
		return dto;
	}
//
//	public static List<ProductDtoParticularItem> toDtoList(List<ProductSF> product) {
//		return product.stream().map(ProductMapper::toDto).collect(Collectors.toList());
//	}

	public static List<Object> toDtoList(List<ProductSF> products, boolean isAllItems) {
		if (products != null) {
			if (isAllItems) {
				return products.stream().map(ProductMapper::toDtoAll).collect(Collectors.toList());
			} else {
				return products.stream().map(ProductMapper::toDto).collect(Collectors.toList());
			}
		} else {
			return null;
		}

	}

	public static ProductDtoParticularItem toDto(Product product) {
		ProductDtoParticularItem dto = new ProductDtoParticularItem();
		dto.setBrandName(product.getBrandName());
		dto.setPrice(product.getPrice());
		dto.setProductTitle(product.getProductTitle());
		dto.setRam(product.getRam());
		dto.setRom(product.getRom());
		dto.setSeriesName(product.getSeriesName());
		dto.setWarranty(product.getWarranty());
		dto.setQuantity(product.getQuantity());
		dto.setLocation(product.getLocation());
		return dto;
	}

	public static List<ProductDtoParticularItem> toDtoList(Product product) {
		List<ProductDtoParticularItem> dtoList = new ArrayList<>();
		dtoList.add(toDto(product));
		return dtoList;
	}

	public static List<ProductDtoParticularItem> toDtoList1(List<Product> products) {
		return products.stream().map(product -> toDto(product)).collect(Collectors.toList());
	}

}
