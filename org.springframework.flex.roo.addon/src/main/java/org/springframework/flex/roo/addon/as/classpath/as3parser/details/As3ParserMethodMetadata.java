package org.springframework.flex.roo.addon.as.classpath.as3parser.details;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.flex.roo.addon.as.classpath.as3parser.As3ParserUtils;
import org.springframework.flex.roo.addon.as.classpath.as3parser.CompilationUnitServices;
import org.springframework.flex.roo.addon.as.classpath.details.MethodMetadata;
import org.springframework.flex.roo.addon.as.classpath.details.metatag.MetaTagMetadata;
import org.springframework.flex.roo.addon.as.model.ASTypeVisibility;
import org.springframework.flex.roo.addon.as.model.ActionScriptSymbolName;
import org.springframework.flex.roo.addon.as.model.ActionScriptType;
import org.springframework.roo.support.util.Assert;

import uk.co.badgersinfoil.metaas.dom.ASArg;
import uk.co.badgersinfoil.metaas.dom.ASMetaTag;
import uk.co.badgersinfoil.metaas.dom.ASMethod;
import uk.co.badgersinfoil.metaas.dom.Statement;

public class As3ParserMethodMetadata implements MethodMetadata {
	
	private ActionScriptSymbolName methodName;
	private ActionScriptType returnType;
	private String declaredByMetadataId;
	private List<MetaTagMetadata> metaTags = new ArrayList<MetaTagMetadata>();
	private String body = "";
	private Map<ActionScriptSymbolName, ActionScriptType> params = new LinkedHashMap<ActionScriptSymbolName, ActionScriptType>();
	private ASTypeVisibility visibility;
	
	public As3ParserMethodMetadata(
			String declaredByMetadataId,
			ASMethod method,
			CompilationUnitServices compilationUnitServices) {
		Assert.notNull(declaredByMetadataId, "Declared by metadata ID required");
		Assert.notNull(method, "Method declaration required");
		Assert.notNull(compilationUnitServices, "Compilation unit services required");
		
		this.declaredByMetadataId = declaredByMetadataId;
		
		this.methodName = new ActionScriptSymbolName(method.getName());
		
		this.returnType = As3ParserUtils.getActionScriptType(compilationUnitServices.getCompilationUnitPackage(), compilationUnitServices.getImports(), method.getType());
		
		this.visibility = As3ParserUtils.getASTypeVisibility(method.getVisibility());
		
		List<ASMetaTag> metaTagList = method.getAllMetaTags();
		if (metaTagList != null) {
			for (ASMetaTag metaTag : metaTagList) {
				As3ParserMetaTagMetadata md = new As3ParserMetaTagMetadata(metaTag);
				metaTags.add(md);
			}
		}
		
		List<Statement> statements = method.getStatementList();
		for (Statement statement : statements) {
			this.body += statement.toString();
		}
		
		List<ASArg> args = method.getArgs();
		for(ASArg arg : args) {
			ActionScriptType paramType = As3ParserUtils.getActionScriptType(compilationUnitServices.getCompilationUnitPackage(), compilationUnitServices.getImports(), arg.getType());
			params.put(new ActionScriptSymbolName(arg.getName()), paramType);
		}
	}

	public ActionScriptSymbolName getMethodName() {
		return this.methodName;
	}

	public ActionScriptType getReturnType() {
		return this.returnType;
	}

	public List<MetaTagMetadata> getMetaTags() {
		return this.metaTags;
	}

	public String getBody() {
		return this.body;
	}

	public List<ActionScriptSymbolName> getParameterNames() {
		return new ArrayList<ActionScriptSymbolName>(params.keySet());
	}

	public List<ActionScriptType> getParameterTypes() {
		return new ArrayList<ActionScriptType>(this.params.values());
	}

	public String getDeclaredByMetadataId() {
		return declaredByMetadataId;
	}

	public ASTypeVisibility getVisibility() {
		return this.visibility;
	}

}