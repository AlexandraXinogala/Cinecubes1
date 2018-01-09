package HelpTask;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.List;

import CubeMgr.CubeBase.Dimension;
import CubeMgr.CubeBase.Hierarchy;
import CubeMgr.CubeBase.Level;
import CubeMgr.CubeBase.LinearHierarchy;
import CubeMgr.CubeBase.CubeQuery;

/**
 * @author  Asterix
 */
public abstract class ExtractionMethod {
    
    private Result Res;
    
    public ExtractionMethod(){
    	Res = new Result();
    }

   public boolean setResult(ResultSet resultSet) {
	   Res=new Result();
   		return Res.createResultArray(resultSet);
   }
   
   public Result getResult() {
	   return Res;
   }
    abstract public String toString();

    abstract public boolean compareExtractionMethod(ExtractionMethod toCompare);
    
    
    public String[][] getResultArray(){
    	return Res.getResultArray();
    }
    
    
    public TreeSet<String> getRowPivot(){
    	return Res.getRowPivot();
    }
    
    public TreeSet<String> getColPivot(){
    	return Res.getColPivot();
    }
    
    public String getTitleosColumns(){
		return Res.getTitleosColumns();
	}
    
    abstract public void addSelectClauseMeasure(String aggregationFuction, String attribute);
    abstract public void addWhereClause(String[] index);
    abstract public void addGroupByClause(String[] index);
    abstract public void addFromClause(String[] index);
    
    public void produceExtractionMethod(CubeQuery cubeQuery)  {
		if(cubeQuery.getListMeasure().get(0).getAttribute() !=null ) 
			addSelectClauseMeasure(cubeQuery.getAggregateFunction(),cubeQuery.getListMeasure().get(0).getAttribute().getName());
		else
			addSelectClauseMeasure(cubeQuery.getAggregateFunction(),"");
		HashSet<String> FromTables=new HashSet<String>();
		
		/*Create WhereClausse */
		for(String[] sigmaExpr: cubeQuery. getSigmaExpressions()){
			for(int i=0;i<cubeQuery.getBasicStoredCube().getListDimension().size();i++){
				Dimension dimension=cubeQuery.getBasicStoredCube().getListDimension().get(i);
				String[] tmp=sigmaExpr[0].split("\\.");
				if(dimension.name.equals(tmp[0])){
					 /* FOR JOIN WITH Basic CUBE*/
					 String toaddJoin[]=new String[3];
					 toaddJoin[0]=cubeQuery.getBasicStoredCube().getDimensionRefField().get(i);
					 toaddJoin[1]="=";
					 toaddJoin[2]=dimension.getTableName()+"."+((LinearHierarchy)dimension.getHier().get(0)).lvls.get(0).lvlAttributes.get(0).getAttribute().getName();
					 addWhereClause(toaddJoin);
					 
					 FromTables.add(dimension.getTableName());
					 
					 /* Add the Sigma Expression */
					 ArrayList<Hierarchy> current_hierachy=dimension.getHier();
					 String toaddSigma[]=new String[3];
					 toaddSigma[0]=dimension.getTableName()+".";
					 for(int k=0;k<current_hierachy.size();k++){//for each hierarchy of dimension
						List<Level> current_lvls=current_hierachy.get(k).lvls;
						for(int l=0;l<current_lvls.size();l++){
							if(current_lvls.get(l).getName().equals(tmp[1])){
								toaddSigma[0]+=current_lvls.get(l).lvlAttributes.get(0).getAttribute().getName();
							}
						}
					}
					toaddSigma[1]=sigmaExpr[1];
					toaddSigma[2]=sigmaExpr[2];
					addWhereClause(toaddSigma);					 
				}
			}
		}
		
		/*Create From clause */
		String[] tbl_tmp=new String[1];
		tbl_tmp[0]="";
		if(cubeQuery.getBasicStoredCube()!=null) tbl_tmp[0]=cubeQuery.getBasicStoredCube().FactTable(). getTableName();
		addFromClause(tbl_tmp);
		
		for(int i=0;i<FromTables.size();i++){
			String[] toAdd=new String[1];
			toAdd[0]=(String) FromTables.toArray()[i];
			addFromClause(toAdd);
		}
		
		
		/*Create groupClausse*/
		for(String[] gammaExpr: cubeQuery.getGammaExpressions()){
			if(gammaExpr[0].length()==0) {
				String[] toadd=new String[1];
				toadd[0]=gammaExpr[1];
				addGroupByClause(toadd);
			}
			else{
				for(int i=0;i<cubeQuery.getBasicStoredCube().getListDimension().size();i++){
					Dimension dimension=cubeQuery.getBasicStoredCube().getListDimension().get(i);
					if(dimension.name.equals(gammaExpr[0])){
						String[] toadd=new String[1];
						toadd[0]=dimension.getTableName()+".";
						ArrayList<Hierarchy> current_hierachy=dimension.getHier();
						for(int k=0;k<current_hierachy.size();k++){//for each hierarchy of dimension
							List<Level> current_lvls=current_hierachy.get(k).lvls;
							for(int l=0;l<current_lvls.size();l++){
								if(current_lvls.get(l).getName().equals(gammaExpr[1])){
									/* FOR JOIN WITH Basic CUBE*/
									 String toaddJoin[]=new String[3];
									 toaddJoin[0]=cubeQuery.getBasicStoredCube().getDimensionRefField().get(i);
									 toaddJoin[1]="=";
									 toaddJoin[2]=dimension.getTableName()+"."+((LinearHierarchy)dimension.getHier().get(0)).lvls.get(0).lvlAttributes.get(0).getAttribute().getName();
									 addWhereClause(toaddJoin);
									 String[] toAddfrom=new String[1];
									 toAddfrom[0]=dimension.getTableName();
									 if(FromTables.contains(dimension.getTableName())==false) 
										 addFromClause(toAddfrom);
									
									toadd[0]+=current_lvls.get(l).lvlAttributes.get(0).getAttribute().getName();
								}
							}
						}
						
						addGroupByClause(toadd);
					}
				}
			}
		}
	}
}
