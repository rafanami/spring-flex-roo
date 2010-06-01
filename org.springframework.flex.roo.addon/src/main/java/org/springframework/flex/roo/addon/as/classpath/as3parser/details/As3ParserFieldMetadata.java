package org.springframework.flex.roo.addon.as.classpath.as3parser.details;

import java.util.ArrayList;
import java.util.List;

import org.springframework.flex.roo.addon.as.classpath.as3parser.As3ParserUtils;
import org.springframework.flex.roo.addon.as.classpath.as3parser.CompilationUnitServices;
import org.springframework.flex.roo.addon.as.classpath.details.ASFieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.AbstractASFieldMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.ASMetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.dom.ASClassType;
import uk.co.badgersinfoil.metaas.dom.ASField;
import uk.co.badgersinfoil.metaas.dom.ASMetaTag;

public class As3ParserFieldMetadata extends AbstractASFieldMetadata {

	private ActionScriptType fieldType;
	private ActionScriptSymbolName fieldName;
	private ASTypeVisibility visibility;
	private List<ASMetaTagMetadata> metaTags = new ArrayList<ASMetaTagMetadata>();
	private String declaredByMetadataId;
	
	@SuppressWarnings("unchecked")
	public As3ParserFieldMetadata(
			String declaredByMetadataId,
			ASField field,
			CompilationUnitServices compilationUnitServices) {
		Assert.notNull(declaredByMetadataId, "Declared by metadata ID required");
		Assert.notNull(field, "ActionScript field is required");
		Assert.notNull(compilationUnitServices, "Compilation unit services are required");
		
		this.setDeclaredByMetadataId(declaredByMetadataId);
		
		this.fieldType = As3ParserUtils.getActionScriptType(compilationUnitServices.getCompilationUnitPackage(), compilationUnitServices.getImports(), field.getType());
		this.setFieldName(new ActionScriptSymbolName(field.getName()));
		this.visibility = As3ParserUtils.getASTypeVisibility(field.getVisibility());

		for(ASMetaTag metaTag : (List<ASMetaTag>)field.getAllMetaTags()) {
			metaTags.add(new As3ParserMetaTagMetadata(metaTag));
		}
	}

	@Override
	public String getDeclaredByMetadataId() {
		return declaredByMetadataId;
	}

	@Override
	public ActionScriptSymbolName getFieldName() {
		return fieldName;
	}

	public ActionScriptType getFieldType() {
		return fieldType;
	}

	public List<ASMetaTagMetadata> getMetaTags() {
		return metaTags;
	}

	public ASTypeVisibility getVisibility() {
		return visibility;
	}
	
	public static void addField(CompilationUnitServices compilationUnitServices, 
			ASClassType clazz, ASFieldMetadata field, boolean permitFlush) {
		
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		Assert.notNull(clazz, "Class required");
		Assert.notNull(field, "Field required");
		
		// Import the field type into the compilation unit
		As3ParserUtils.importTypeIfRequired(compilationUnitServices, field.getFieldType());
		
		// Add the field
		ASField newField = clazz.newField(field.getFieldName().getSymbolName(), As3ParserUtils.getAs3ParserVisiblity(field.getVisibility()), field.getFieldType().getSimpleTypeName());
		
		// Add meta tags to the field
		for(ASMetaTagMetadata metaTag : field.getMetaTags()) {
			As3ParserMetaTagMetadata.addMetaTagToElement(compilationUnitServices, metaTag, newField, false);
		}
		
		if (permitFlush) {
			compilationUnitServices.flush();
		}
	}
	
	public static void updateField(CompilationUnitServices compilationUnitServices, ASClassType clazz, ASFieldMetadata field, boolean permitFlush) {
		
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		Assert.notNull(clazz, "Class required");
		Assert.notNull(field, "Field required");
		
		// Import the field type into the compilation unit
		As3ParserUtils.importTypeIfRequired(compilationUnitServices, field.getFieldType());
		
		ASField existingField = clazz.getField(field.getFieldName().getSymbolName());
		
		existingField.setVisibility(As3ParserUtils.getAs3ParserVisiblity(field.getVisibility()));
		
		existingField.setType(field.getFieldType().getSimpleTypeName());
		
		// Add meta tags to the field
		for(ASMetaTagMetadata metaTag : field.getMetaTags()) {
			if (existingField.getFirstMetatag(metaTag.getName()) != null) {
				As3ParserMetaTagMetadata.addMetaTagToElement(compilationUnitServices, metaTag, existingField, false);
			}
		}
	}
	
	public static void removeField(CompilationUnitServices compilationUnitServices, ASClassType clazz, ActionScriptSymbolName fieldName, boolean permitFlush) {
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		Assert.notNull(clazz, "Class required");
		Assert.notNull(fieldName, "Field name required");
		
		Assert.notNull(clazz.getField(fieldName.getSymbolName()), "Could not locate field '" + fieldName + "' to delete");
		
		clazz.removeField(fieldName.getSymbolName());
		
		if(permitFlush) {
			compilationUnitServices.flush();
		}
	}

	private void setDeclaredByMetadataId(String declaredByMetadataId) {
		this.declaredByMetadataId = declaredByMetadataId;
	}

	private void setFieldName(ActionScriptSymbolName fieldName) {
		this.fieldName = fieldName;
	}
	
	
}
