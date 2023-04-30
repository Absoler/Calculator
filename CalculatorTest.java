public class CalculatorTest{
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        String ret;
        double res;
        // set and get test
        calculator.calculation("define a = 1");
        res = Double.valueOf(calculator.calculation("a"));
        assert res==1 : "res = " + String.valueOf(res);
        System.out.println("--- pass basic set/get test");

        // arithmetic operations test
        res = Double.valueOf(calculator.calculation("(2) ^ ( 5+ 2*3.5)"));
        assert res==4096 : "res = " + String.valueOf(res);
        res = Double.valueOf(calculator.calculation("define b2b = (4^-0.5 + 4) /0.9 + a"));
        assert res==6 : "res = " + String.valueOf(res);
        res = Double.valueOf(calculator.calculation("b2b ^ 2"));
        assert res==36 : "res = " + String.valueOf(res);
        System.out.println("--- pass arithmetic operation test");

        // exception test
        ret = calculator.calculation("c");
        assert ret.equals("UndefineException") : "ret = " + ret;
        ret = calculator.calculation("1.5 # 3");
        assert ret.equals("BadTokenException") : "ret = " + ret;
        ret = calculator.calculation("2)");
        assert ret.equals("SyntaxErrorException") : "ret = " + ret;
        ret = calculator.calculation("2 ^ (1+5)");
        assert ret.equals("SyntaxErrorException") : "ret = " + ret;
        System.out.println("--- pass exception test");
    }
}