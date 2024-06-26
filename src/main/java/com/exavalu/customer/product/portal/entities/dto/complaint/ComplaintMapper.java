package com.exavalu.customer.product.portal.entities.dto.complaint;

import java.util.List;
import java.util.stream.Collectors;

import com.exavalu.customer.product.portal.entities.salesforce.Complaint;

public class ComplaintMapper {
		public static ComplaintDto toDto(Complaint complaint) {
			ComplaintDto dto = new ComplaintDto();
			dto.setAction(complaint.getAction());
			dto.setComplaint_category(complaint.getComplaint_category());
			dto.setComplaintId(complaint.getName());
			dto.setCustomerId(complaint.getCustomerId());
			dto.setDate_of_complaint(complaint.getDate_of_complaint());
			dto.setDescription(complaint.getDescription());
			dto.setOrderId(complaint.getOrderId());
			dto.setProductTitle(complaint.getProductTitle());
			dto.setQuantity(complaint.getQuantity());
			dto.setStatus(complaint.getStatus());
			dto.setReason(complaint.getReason());
			return dto;
		}
		
		public static List<ComplaintDto> toDtoList(List<Complaint> complaints){
			return complaints.stream().map(ComplaintMapper::toDto).collect(Collectors.toList());
		}
}
