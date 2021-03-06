<#include "common.ftl"/>
package ${package};

<#list imports as i>
import ${i};
</#list>

<#if entity.comment?has_content>
@${gen.annoComment(entity)}("${entity.comment}")
</#if>
<#if entity.table?has_content>
@Table("${entity.table}")
</#if>
<#if entity.foreignKeyMap?has_content>
@ForeignKeys({
<#list entity.foreignKeyMap?keys as k>
	@FK(${entity.getForeignKeyDefine(entity.foreignKeyMap[k])})<#if k_has_next>,</#if>
</#list>
})
</#if>
<#if entity.indexKeyMap?has_content || entity.uniqueKeyMap?has_content>
@Indexes({
<#list entity.indexKeyMap?keys as k>
	@Index(name="${k}", fields={ <#list entity.indexKeyMap[k] as p>"${p.name}"<#if p_has_next>, </#if></#list> })<#if k_has_next || entity.uniqueKeyMap?has_content>,</#if>
</#list>
<#list entity.uniqueKeyMap?keys as k>
	@Index(name="${k}", fields={ <#list entity.uniqueKeyMap[k] as p>"${p.name}"<#if p_has_next>, </#if></#list> }, unique=true)<#if k_has_next>,</#if>
</#list>
})
</#if>
<#if entity.joinMap?has_content>
@Joins({
<#list entity.joinMap?keys as k>
	@Join(${entity.getJoinDefine(entity.joinMap[k])})<#if k_has_next>,</#if>
</#list>
})
</#if>
public class ${name} <#if entity.baseBeanClass?has_content>extends ${class_name(entity.baseBeanClass)}</#if> implements Serializable<#if entity.baseInterfaces?has_content><#list entity.baseInterfaces as i>, ${class_name(i)}</#list></#if> {

	private static final long serialVersionUID = ${svuid?c}L;

	/**
	 * Constructor
	 */
	public ${name}() {
		super();
	}

	/*----------------------------------------------------------------------*
	 * Constants
	 *----------------------------------------------------------------------*/
<#list entity.propertyList as p>
	public static final String ${p.uname} = "${p.name}";
</#list>

	public static final String[] _COLUMNS_ = new String[] {
<#list entity.columnList as p>
			${p.uname}<#if p_has_next>,</#if>
</#list>
		};

<#if entity.joinList?has_content>
	public static final String[] _JOINS_ = new String[] {
<#list entity.joinList as p>
			${p.uname}<#if p_has_next>,</#if>
</#list>
		};
</#if>

<#if entity.joinMap?has_content>
<#list entity.joinMap?keys as k>
	public static final String _JOIN_${k?upper_case}_ = "${k}";
</#list>
</#if>

	/*----------------------------------------------------------------------*
	 * Properties
	 *----------------------------------------------------------------------*/
<#list entity.propertyList as p>
<#if entity.isIdentity(p.name)>
	@Id<#if entity.identityDefine?has_content>(${entity.identityDefine})</#if>
<#elseif p.dbColumn>
	<#if p.primaryKey>
	@PK
	</#if>
	@Column<#if p.columnDefine?has_content>(${p.columnDefine})</#if>
<#elseif p.joinColumn>
	@JoinColumn(name="${p.joinName}", field="${p.joinField}")
</#if>
<#if p.comment?has_content>
	@${gen.annoComment(entity)}("${p.comment}")
</#if>
	${p.modifier} ${p.simpleJavaType} ${p.name}<#if p.initValue?has_content> = ${p.initValue}</#if>;

</#list>

	/*----------------------------------------------------------------------*
	 * Getter & Setter
	 *----------------------------------------------------------------------*/
<#list entity.properties as p>
<#if p.property>
	/**
	 * @return the ${p.name}
	 */
<#-- validation -->
<#assign stype = p.simpleJavaType/>
<#assign type = p.elementType/>
<#if gen.castErrorType(type)>
	@CastErrorValidate(msgId=${gen.validatorMsgId('cast-' + type)})
</#if>
<#if p.validatorList?has_content>
<#list p.validatorList as v>
	@${gen.validatorAnnotation(v)}
</#list>
</#if>
<#-- validation -->
	public ${p.simpleJavaType} <#if p.simpleJavaType == 'boolean'>is<#else>get</#if>${p.name?cap_first}() {
	<#assign getterTrim = "" />
	<#if p.getterTrim??>
		<#if p.getterTrim?has_content>
			<#assign getterTrim = p.getterTrim/>
		</#if>
	</#if>
	<#if p.getterCode?has_content>
		${p.getterCode}
	<#elseif getterTrim?has_content>
		return ${getterTrim}(${p.name});
	<#else>
		return ${p.name};
	</#if>
	}

	/**
	 * @param ${p.name} the ${p.name} to set
	 */
	public void set${p.name?cap_first}(${p.simpleJavaType} ${p.name}) {
	<#assign setterTrim = "" />
	<#if p.setterTrim??>
		<#if p.setterTrim?has_content>
			<#assign setterTrim = p.setterTrim/>
		</#if>
	<#elseif entity.trimString?has_content && p.simpleJavaType == "String">
		<#assign setterTrim = entity.trimString />
	<#elseif entity.trimList?has_content && (p.simpleJavaType?matches("List<.*>") || p.simpleJavaType?matches("Set<.*>"))>
		<#assign setterTrim = entity.trimList />
	</#if>
	<#if p.setterCode?has_content>
		${p.setterCode}
	<#elseif setterTrim?has_content>
		this.${p.name} = ${setterTrim}(${p.name});
	<#else>
		this.${p.name} = ${p.name};
	</#if>
	}

<#elseif p.joinColumn>
	/**
	 * @return the ${p.name}
	 */
	@Override
	@JoinColumn(name="${p.joinName}", field="${p.joinField}")
	public ${p.simpleJavaType} <#if p.simpleJavaType == 'boolean'>is<#else>get</#if>${p.name?cap_first}() {
		return super.<#if p.simpleJavaType == 'boolean'>is<#else>get</#if>${p.name?cap_first}();
	}

</#if>
</#list>

	/**
	 * copy properties from the specified object.
	 * @param src the source object to copy
	 */
	public void copy(${name} src) {
<#list entity.propertyList as p>
		this.${p.name} = src.${p.name};
</#list>
<#if entity.baseBeanClass?has_content>
		super.copy(src);
</#if>
	}

	/*----------------------------------------------------------------------*
	 * Overrides
	 *----------------------------------------------------------------------*/
	/**
	 * Creates and returns a copy of this object.
	 * @return the copy object
	 */
	@Override
	public ${name} clone() {
		${name} copy = new ${name}();
		
		copy.copy(this);

		return copy;
	}

	/**
	 * @return  a hash code value for this object.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(<#list entity.primaryKeyList as p>${p.name}<#if p_has_next>,</#if></#list>);
	}

	/**
	 * @return  <code>true</code> if this object is the same as the obj argument; 
	 * 			<code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		${name} rhs = (${name})obj;
<#if 'false' == props['entity.equals.pkey']!'true'>
	<#assign pl = entity.propertyList/>
		return Objects.equalsBuilder()
<#list pl as p>
				.append(${p.name}, rhs.${p.name})
</#list>
<#if entity.baseBeanClass?has_content>
				.appendSuper(super.equals(rhs))
</#if>
<#else>
	<#assign pl = entity.primaryKeyList/>
		return Objects.equalsBuilder()
<#list pl as p>
				.append(${p.name}, rhs.${p.name})
</#list>
</#if>		
				.isEquals();
	}

	/**
	 * @return  a string representation of the object.
	 */
	@Override
	public String toString() {
		return Objects.toStringBuilder()
<#list entity.propertyList as p>
				.append(${p.uname}, ${p.name})
</#list>
<#if entity.baseBeanClass?has_content>
				.appendSuper(super.toString())
</#if>
				.toString();
	}
}

