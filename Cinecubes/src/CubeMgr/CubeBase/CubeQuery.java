package CubeMgr.CubeBase;

import java.util.ArrayList;

import CubeMgr.StarSchema.SqlQuery;

public class CubeQuery extends Cube {

	/**
	 * @uml.property  name="gammaExpressions"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="[Ljava.lang.String;"
	 */
	private ArrayList<String[]> GammaExpressions; // 0->dimension_name, 1->level
													// of dimension
	/**
	 * @uml.property  name="sigmaExpressions"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="[Ljava.lang.String;"
	 */
	private ArrayList<String[]> SigmaExpressions; // 0->dimension_name.level, 1->
													// operator , 2->VALUE
	/**
	 * @uml.property  name="aggregateFunction"
	 */
	private String AggregateFunction;
	/**
	 * @uml.property  name="sqlQuery"
	 * @uml.associationEnd  
	 */
	private SqlQuery sqlQuery;
	/**
	 * @uml.property  name="referCube"
	 * @uml.associationEnd  
	 */
	private BasicStoredCube referCube;
	
	public CubeQuery(String name, String aggregateFunction, Measure msrToAdd, String[][] gamma, String[][] sigma ){
		super(name);
	}
	
	public void setBasicStoredCube(BasicStoredCube referCube){
		this.referCube = referCube;
	}
	
	public BasicStoredCube getBasicStoredCube() {
		return referCube;
	}
		
	public void setAggregateFunction(String AggregateFunction){
		this.AggregateFunction = AggregateFunction;
	}
	
	public String getAggregateFunction() {
		return AggregateFunction;
	}
	
	public void setGammaExpressions( ArrayList<String[]> GammaExpressions){
		this.GammaExpressions = GammaExpressions;
	}
	
	public ArrayList<String[]> getGammaExpressions() {
		return GammaExpressions;
	}
	
	public void setSigmaExpressions( ArrayList<String[]> SigmaExpressions){
		this.SigmaExpressions = SigmaExpressions;
	}
	
	public ArrayList<String[]> getSigmaExpressions() {
		return SigmaExpressions;
	}
	
	public Level getParentLevel(String dimension, String lvlname) {
		return referCube.getParentLevel(dimension,lvlname);
	}
	
	public String getDimensionRefField(int index){
		return referCube.getDimensionRefField().get(index);
	}
 
	public CubeQuery(String Name) {
		super(Name);
		GammaExpressions = new ArrayList<String[]>();
		SigmaExpressions = new ArrayList<String[]>();
	}

	public void addGammaExpression(String table, String field) {
		String toadd[] = new String[2];
		toadd[0] = table;
		toadd[1] = field;
		GammaExpressions.add(toadd);
	}

	public SqlQuery getSqlQuery(){
		return sqlQuery;
	}
	
	public void setSqlQuery(SqlQuery sqlQuery){
		this.sqlQuery =  sqlQuery;
	}
	
	public void addSigmaExpression(String tablefield, String operator,
			String value) {
		String toadd[] = new String[3];
		toadd[0] = tablefield;
		toadd[1] = operator;
		toadd[2] = value;
		SigmaExpressions.add(toadd);
	}

	
	public String toString() {
		String ret_value = "Name:" + this.name + "\nAggregate Function : "
				+ AggregateFunction + "\n";
		if (this.Msr.size() > 0 && this.Msr.get(0) != null
				&& this.Msr.get(0).getAttribute() != null)
			ret_value += "Measure : " + this.Msr.get(0).getAttribute().getName() + "\n";
		ret_value += "Gamma Expression: ";
		for (int i = 0; i < GammaExpressions.size(); i++) {
			if (i > 0)
				ret_value += " , ";
			if (GammaExpressions.get(i)[0].length() > 0)
				ret_value += GammaExpressions.get(i)[0] + ".";
			ret_value += GammaExpressions.get(i)[1];
		}

		ret_value += "\nSigma Expression: ";
		for (int i = 0; i < SigmaExpressions.size(); i++) {
			if (i > 0)
				ret_value += " AND ";
			ret_value += SigmaExpressions.get(i)[0]
					+ SigmaExpressions.get(i)[1] + SigmaExpressions.get(i)[2];
		}
		return ret_value;
	}

	public void addMeasure(int id, String name){
		Measure msrToAdd = new Measure(id, name, null);
		Msr.add( msrToAdd);
	}
	
	
}
