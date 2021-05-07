package algorithm;

import ilog.concert.*;
import ilog.cplex.IloCplex;

import tasks.Knapsack;

import java.util.ArrayList;
import java.util.List;

public class CplexSolveForDual {
    private final Knapsack problem;
    protected IloCplex model;
    protected IloIntVar[] x;
    public int objectiveValue;
    public double gap;
    public String status;
    List<IloRange> constraints;
    double[] prices;

    public CplexSolveForDual(Knapsack problem) throws IloException {
        this.problem = problem;
        this.model = new IloCplex();
        this.x = new IloIntVar[problem.dimension];
    }

    protected void addVariables() throws IloException {
        for (int j = 0; j < problem.dimension; j++) {
            x[j] = (IloIntVar) model.numVar(0, 1, IloNumVarType.Float, "x[" + j + "]");
        }
    }

    //The following code creates the constraints for the problem.
    protected void addConstraints() throws IloException {
        constraints = new ArrayList<IloRange>();
        for (int i = 0; i < problem.nConstraint; i++) {
            IloLinearNumExpr expr_1 = model.linearNumExpr();
            for (int j = 0; j < problem.dimension; j++) {
                expr_1.addTerm(x[j], problem.weights[i][j]);
            }
            constraints.add(model.addLe(expr_1, problem.capacity[i]));

        }
    }

    //The following code creates the objective function for the problem.
    protected void addObjective() throws IloException {
        IloLinearNumExpr objective = model.linearNumExpr();

        for (int j = 0; j < problem.dimension; j++) {
            objective.addTerm(x[j], problem.prices[j]);
        }

        IloObjective Obj = model.addObjective(IloObjectiveSense.Maximize, objective);
    }
    public void setParam(double maxRunTime,double gap) throws IloException {
        model.setParam(IloCplex.DoubleParam.TiLim, maxRunTime);
        model.setParam(IloCplex.DoubleParam.EpGap, gap);
    }

    public void solveModel() throws IloException {
        addVariables();
        addObjective();
        addConstraints();
//        model.exportModel(problem.fileName+".lp");
        setParam(100.0, 0);
        model.solve();
        if (model.getStatus() == IloCplex.Status.Feasible | model.getStatus() == IloCplex.Status.Optimal) {

            System.out.println("Solution status = "+ model.getStatus());
//            int sumPrices = 0;
//            int chooseitemNum= 0;
//            for (int j = 0; j < problem.dimension; j++) {
//                if (model.getValue(x[j]) == 1) {
////                    System.out.println("Item " + j + " - price: " + problem.prices[j]);
//                    sumPrices += problem.prices[j];
//                    chooseitemNum += 1;
//                }
//            }
            /*打印对偶变量值*/
            prices = new double[constraints.size()];
            for (int i=0;i<constraints.size();i++) {
                prices[i] = +model.getDual(constraints.get(i));
            }
            objectiveValue = (int) model.getObjValue();
            gap = model.getMIPRelativeGap();
            status = model.getStatus().toString();
//            System.out.printf("目标函数值为%d:, 选择了%d个商品%n", objectiveValue, chooseitemNum);
            for (int i=0;i<constraints.size();i++) {
                 System.out.println("dual constraint "+(i+1)+"  = "+model.getDual(constraints.get(i)));
//                System.out.println("slack constraint "+(i+1)+" = "+model.getSlack(constraints.get(i)));
            }
        } else {
            System.out.println("The problem status is:" + model.getStatus());
        }
    }

    public double[] getPrices(){
        return prices;
    }

}
