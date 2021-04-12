package algorithm;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.cplex.IloCplex;

import tasks.Knapsack;

public class CplexSolve {
    private final Knapsack problem;
    protected IloCplex model;
    protected IloIntVar[] x;
    public int objectiveValue;
    public double gap;

    public CplexSolve(Knapsack problem) throws IloException {
        this.problem = problem;
        this.model = new IloCplex();
        this.x = new IloIntVar[problem.dimension];
    }

    protected void addVariables() throws IloException {
        for (int j = 0; j < problem.dimension; j++) {
            x[j] = (IloIntVar) model.numVar(0, 1, IloNumVarType.Int, "x[" + j + "]");
        }
    }

    //The following code creates the constraints for the problem.
    protected void addConstraints() throws IloException {

        for (int i = 0; i < problem.nConstraint; i++) {
            IloLinearNumExpr expr_1 = model.linearNumExpr();
            for (int j = 0; j < problem.dimension; j++) {
                expr_1.addTerm(x[j], problem.weights[i][j]);
            }
            model.addLe(expr_1, problem.capacity[i]);
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
        model.exportModel(problem.fileName+".lp");
        setParam(100.0, 0.001);

        model.solve();// questo metodo risolve il problema

        if (model.getStatus() == IloCplex.Status.Feasible | model.getStatus() == IloCplex.Status.Optimal) {
            System.out.println();
            System.out.println("Solution status = "+ model.getStatus());
            System.out.println();
            System.out.println();

            int sumPrices = 0;
            int chooseitemNum= 0;
            for (int j = 0; j < problem.dimension; j++) {
                if (model.getValue(x[j]) == 1) {
                    System.out.println(
                            "Item " + j + " - price: " + problem.prices[j]);
                    sumPrices += problem.prices[j];
                    chooseitemNum += 1;
                }
            }
            objectiveValue = (int) model.getObjValue();
            gap = model.getMIPRelativeGap();
            System.out.printf("目标函数值为%d:, 选择了%d个商品%n", objectiveValue, chooseitemNum);
        } else {
            System.out.println("The problem status is:" + model.getStatus());
        }
    }
}