import java.util.HashMap;

class BadTokenException extends RuntimeException{
    public BadTokenException(){
        super();
    }

    public BadTokenException(int p){
        super("# BadTokenException: parse tokens error at the " + p +"th character");
    }
}

class SyntaxErrorException extends RuntimeException{
    public SyntaxErrorException(){
        super();
    }

    public SyntaxErrorException(int p, Type meet, Type expect){
        super("# SyntaxErrorException: expect " + expect.name() + " but meet " + meet.name() + " at " + p);
    }
}

class UndefineException extends RuntimeException{
    public UndefineException(){
        super();
    }

    public UndefineException(String id){
        super("# UndefineException: undefined identifier " + id);
    }
}

enum Type{
    ADD, SUB, MUL, DIV, EQ, EXP, DOT, LEFT, RIGHT,
    ALPHASTR, DIGITSTR,
    DEFINE,
    NULL, BAD;
    
    public static Type valueOf(int ordinal){
        if ( ordinal<0 || ordinal>=9 ){
            throw new IndexOutOfBoundsException("invalid ordinal");
        }
        return values()[ordinal];
    }
}

class Token{
    String literal;
    Type type;

    int offset; // moved step of getting this token,
    
}

public class Calculator{

    public String input;
    int p, len; // p is the global pointer to `input`, indicate the place where we analyze now
    HashMap<String, Double> table;  // identifier <key, value> storage

    Calculator(){
        input = "";
        p = 0;
        table = new HashMap<String, Double>();
    }

    boolean isNumberFirst(char c){
        // whether `c` is a valid start of a number
        return Character.isDigit(c) || c == '+' || c=='-';
    }

    Token getToken(){
        // get a token, will move `p` meanwhile
        /*
         * token type:
         * 1. alpha string: [letter]
         * 2. digit string: [digit]
         * 3. special characters: [+-*\/=^.()]
         * 4. "define"
         */
        Token res = new Token();
        int base = p;


        while(p<len && Character.isWhitespace(input.charAt(p))){
            // omit white space
            p += 1;
        }

        if(p == len){
            res.type = Type.NULL;
            return res;
        }

        String token = "";
        if(Character.isDigit(input.charAt(p))){
            do{
                token += input.charAt(p);
                p += 1;
            }while(p<len && Character.isDigit(input.charAt(p)));
            res.literal = token;
            res.type = Type.DIGITSTR;
        }else if(len > p+6 && input.substring(p, p+6).compareTo("define") == 0){
            res.literal = "define";
            res.type = Type.DEFINE;
            p += 6;
        }else if(Character.isLetter(input.charAt(p))){
            do{
                token += input.charAt(p);
                p += 1;
            }while(p<len && Character.isLetter(input.charAt(p)));
            res.literal = token;
            res.type = Type.ALPHASTR;
        }else{
            String specialChars = "+-*/=^.()";
            
            int id = specialChars.indexOf(input.charAt(p));
            if (id!=-1){
                res.type = Type.valueOf(id);
                res.literal = String.valueOf(input.charAt(p));
                p += 1;
            }else{
                res.type = Type.BAD;
                throw new BadTokenException(p);
            }
            
        }
        res.offset = p - base;
        return res;
    }
    void back(int offset){
        // restore position of `p`, used for eliminating the effect of 'getToken()'
        p -= offset;
    }

    char first(){
        // get the next valid character
        int q = p;
        while(q<len && Character.isWhitespace(input.charAt(q))){
            q += 1;
        }
        return input.charAt(q);
    }


    public String calculation(String _input){
        // init current calculation
        input = _input+"#";
        p = 0;
        len = _input.length();
        boolean success = false;


        // perform calculation analysis
        String exp = "";
        try{
            Token first = getToken();
            if(first.type == Type.DEFINE){
                // need to set the value of <exp> to <id>

                String id = identifier();

                Token eq = getToken();
                if (eq.type!=Type.EQ){
                    back(eq.offset);
                    throw new SyntaxErrorException(p, eq.type, Type.EQ);
                }

                exp = String.valueOf(expression());
                table.put(id, Double.valueOf(exp));
            }else{
                // only calculate the next expression
                back(first.offset);
                exp = String.valueOf(expression());
            }
            success = true;
        }catch(BadTokenException e){
            exp = "BadTokenException";
            System.out.println(e.getMessage());
        }catch(SyntaxErrorException e){
            exp = "SyntaxErrorException";
            System.out.println(e.getMessage());
        }catch(UndefineException e){
            exp = "UndefineException";
            System.out.println(e.getMessage());
        }

        if( success && p < len ){
            System.out.println("# SyntaxErrorException: extra input from the " + String.valueOf(p) + "th char");
            exp = "SyntaxErrorException";
        }
        return exp;
    }

    public String identifier(){
        return alphanumericstring();
    }

    public String alphanumericstring(){
        String res = "";
        boolean keep = true;
        do{
            String cur = alphastring();
            Token token = getToken();
            back(token.offset);
            if(token.type == Type.DIGITSTR){
                cur += digitstring();
            }
            res += cur;

            Token tmp = getToken();
            keep = tmp.type == Type.ALPHASTR;
            back(tmp.offset);
        }while(keep);

        return res;
    }

    public String alphastring(){
        Token alphastr = getToken();
        if(alphastr.type!=Type.ALPHASTR){
            back(alphastr.offset);
            throw new SyntaxErrorException(p, alphastr.type, Type.ALPHASTR);
        }
        return alphastr.literal;
    }

    public String digitstring(){
        Token digitstr = getToken();
        if(digitstr.type != Type.DIGITSTR){
            back(digitstr.offset);
            throw new SyntaxErrorException(p, digitstr.type, Type.DIGITSTR);
        }
        return digitstr.literal;
    }

    public double expression(){
        double res = term();
        while(true){
            Token sym = getToken();
            if(sym.type != Type.ADD && sym.type != Type.SUB){
                back(sym.offset);
                break;
            }
            double next = term();
            if(sym.type == Type.ADD){
                res += next;
            }else{
                res -= next;
            }
        }
        return res;
    }

    public double term(){
        double res = factor();
        while(true){
            Token sym = getToken();
            if(sym.type != Type.MUL && sym.type != Type.DIV){
                back(sym.offset);
                break;
            }
            double next = factor();
            if(sym.type == Type.MUL){
                res *= next;
            }else{
                res = res/next;
            }
        }
        return res;
    }

    public double factor(){
        double base = 0;
        char firstChar = first();
        if(Character.isLetter(firstChar) || isNumberFirst(firstChar)){
            /*
                <factor> = <identifier>
                <factor> = <number>
                <factor> = <identifier> | <number> '^' <identifier> | <number>
             */
            if(Character.isLetter(firstChar)){
                String id = identifier();
                if(table.containsKey(id)){
                    base = table.get(id);
                }else{
                    throw new UndefineException(id);
                }
            }else if(isNumberFirst(firstChar)){
                base = number();
            }
            
            if(first()=='^'){
                getToken();
                double exp;
                if(isNumberFirst(first())){
                    exp = number();
                }else if(Character.isLetter(first())){
                    String id = identifier();
                    if(table.containsKey(id)){
                        exp = table.get(id);
                    }else{
                        throw new UndefineException(id);
                    }
                }else{
                    throw new SyntaxErrorException(p, getToken().type, Type.ALPHASTR);
                }
                base = Math.pow(base, exp);
            }
            return base;
        }else if(firstChar=='('){
            /*  
                <factor> = '(' <expression> ')' { '^' <identifier>|<number>} ')'
                <factor> = '(' <expression> ')' '^' '(' <expression> ')' 
            */
            getToken();
            base = expression();
            Token right = getToken();
            
            if(right.type!=Type.RIGHT){
                back(right.offset);
                throw new SyntaxErrorException(p, right.type, Type.RIGHT);
            }

            if(first()=='^'){
                double exp;
                getToken();
                if(Character.isDigit(first())){
                    exp = number();
                    // the production in the doc seems wrong, has an extra ')' at the end
                }else if(Character.isLetter(first())){
                    String id = identifier();
                    exp = table.get(id);
                }else if(first()=='('){
                    getToken();
                    exp = expression();
                    Token right2 = getToken();
                    if(right2.type!=Type.RIGHT){
                        back(right2.offset);
                        throw new SyntaxErrorException(p, right2.type, Type.RIGHT);
                    }
                }else{
                    throw new SyntaxErrorException();
                }
                base = Math.pow(base, exp);
            }
            return base;
        }else{
            throw new SyntaxErrorException();
        }
        
    }

    public double number(){
        boolean plus = true;
        if(first()=='+' || first()=='-'){
            Token flag = getToken();
            if (flag.type == Type.SUB){
                plus = false;
            }
        }
        String num = digitstring();
        if(!plus){
            num = "-" + num;
        }
        if(first()=='.'){
            getToken();
            String decimal = digitstring();
            num = num + '.' + decimal;
        }
        return Double.valueOf(num);
    }

}