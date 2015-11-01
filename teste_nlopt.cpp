#include <iostream>
#include <cstdio>
#include <vector>
#include <cmath>
#include <fstream>
#include <nlopt.hpp>

using namespace std;

int counter = 0;

double myvconstraint(const std::vector<double> &x, std::vector<double> &grad, void *data)
{
    int* idx = (int*) data;
    if (!grad.empty()) {
            grad[*idx] = -1;

    }
    return -x[*idx];
}

double myvconstraint1(const std::vector<double> &x, std::vector<double> &grad, void *data)
{
    if (!grad.empty()) {
        for( int i = 0; i < grad.size(); ++i )
            grad[i] = -1.0;

    }
    return -x[0];
}
double myvconstraint2(const std::vector<double> &x, std::vector<double> &grad, void *data)
{
    if (!grad.empty()) {
        for( int i = 0; i < grad.size(); ++i )
            grad[i] = -1.0;
    }
    return -x[1];
}
double myvconstraint3(const std::vector<double> &x, std::vector<double> &grad, void *data)
{
    if (!grad.empty()) {
        for( int i = 0; i < grad.size(); ++i )
            grad[i] = -1.0;
    }
    return -x[2];
}
double myvconstraint4(const std::vector<double> &x, std::vector<double> &grad, void *data)
{
    if (!grad.empty()) {
        for( int i = 0; i < grad.size(); ++i )
            grad[i] = -1.0;
    }
    return -x[3];
}

double myvconstraint5(const std::vector<double> &x, std::vector<double> &grad, void *data)
{
    if (!grad.empty()) {
        for( int i = 0; i < grad.size(); ++i )
            grad[i] = -1.0;
    }
    return -x[4];
}
double myvconstraint6(const std::vector<double> &x, std::vector<double> &grad, void *data)
{
    if (!grad.empty()) {
        for( int i = 0; i < grad.size(); ++i )
            grad[i] = -1.0;
    }
    return -x[5];
}

double xPlus(double x)
{
	if( x < 0.0 ) {
		return 0.0;
    }
	return x;
}

double xPlus_linha(double x)
{
    if( x < 0.0 )
        return 0;
    return 1;
}

double O(double ai, double bi, double aj, double bj)
{
	if( ai >= aj ) {
		return (1.0/pow(bj, 4.0))*pow(xPlus(pow(bj, 2) - pow(ai-aj, 2)), 2);
    }
	return (1.0/pow(bi, 4.0))*pow(xPlus(pow(bi, 2) - pow(ai-aj, 2)), 2);
}



double myvfunc(const std::vector<double> &x, std::vector<double> &grad, void *my_func_data)
{
    counter++;
    int n = x.size()/2;
    int N = x.size();
    cout << "numero: " << n << endl;
    if (!grad.empty()) {
        double q = 0;
        //for( int i = 0; i < N; ++i )
        //    grad[i] = 0.0;

        for( int i = 0; i < N; i += 2 )
            for( int j = i+2; j < N; j += 2 ) {
                q = pow(50, 2.) - pow(x[i]-x[j], 2.);
                grad[i] += (-4. * (x[i]-x[j]) * xPlus(q) * xPlus_linha(q) ) / pow(50., 4.) * O(x[i+1], 50, x[j+1], 50);

                q = pow(50, 2.) - pow(x[i+1]-x[j+1], 2.);
                grad[i+1] += (-4. * (x[i+1]-x[j+1]) * xPlus(q) * xPlus_linha(q)) / pow(50., 4.) * O(x[i], 50, x[j], 50);

                q = pow(50, 2.) - pow(x[i]-x[j], 2.);
                grad[j] += (4. * (x[i]-x[j]) * xPlus(q) * xPlus_linha(q) ) / pow(50., 4.) * O(x[i+1], 50, x[j+1], 50);

                q = pow(50, 2.) - pow(x[i+1]-x[j+1], 2.);
                grad[j+1] += (4. * (x[i+1]-x[j+1]) * xPlus(q) * xPlus_linha(q)) / pow(50., 4.) * O(x[i], 50, x[j], 50);
            }

        for( int i = 0; i < N; ++i )
            grad[i] *= (2./(n*(n-1.)));
    }

    double sum = 0.0;
    for( int i = 0; i < N; i += 2 )
        for( int j = i+2; j < N; j += 2 )
            sum += O(x[i], 50, x[j], 50) * O(x[i+1], 50, x[j+1], 50);
    sum *= (2.0/(n*(n-1)));

    return sum;
}

vector<double> read_elems()
{
    double x = 0.0, y = 0.0;
    vector<double> elems;
    ifstream ifs("points.rect");
    if( ifs ) {
        while( ifs >> x >> y ) {
            cout << x << " " << y << endl;
            elems.push_back(x);
            elems.push_back(y);
        }
    }

    return elems;
}


int main() {
    vector<double> x = read_elems();
    int n = x.size();

	nlopt::opt opt(nlopt::LD_MMA, n);
    vector<double> lb(n, 0);
    vector<double> up(n, 500);
    opt.set_lower_bounds(lb);
    opt.set_upper_bounds(up);


    opt.set_min_objective(myvfunc, NULL);
    /*int q0 = 0;
    opt.add_inequality_constraint(myvconstraint, &q0, 1e-8);
    int q1 = 1;
    opt.add_inequality_constraint(myvconstraint, &q1, 1e-8);
    int q2 = 2;
    opt.add_inequality_constraint(myvconstraint, &q2, 1e-8);
    int q3 = 3;
    opt.add_inequality_constraint(myvconstraint, &q3, 1e-8);
    int q4 = 4;
    opt.add_inequality_constraint(myvconstraint, &q4, 1e-8);
    int q5 = 5;
    opt.add_inequality_constraint(myvconstraint, &q5, 1e-8);
    int q6 = 6;
    opt.add_inequality_constraint(myvconstraint, &q6, 1e-8);
    int q7 = 7;
    opt.add_inequality_constraint(myvconstraint, &q7, 1e-8);*/
    //int q8 = 8;
    //opt.add_inequality_constraint(myvconstraint, &q8, 1e-8);
    //int q9 = 9;
    //opt.add_inequality_constraint(myvconstraint, &q9, 1e-8);
  /*  int q1 = 1;
    opt.add_inequality_constraint(myvconstraint, &q1, 1e-8);
    int q2 = 2;
    opt.add_inequality_constraint(myvconstraint, &q2, 1e-8);
    int q3 = 3;
    opt.add_inequality_constraint(myvconstraint, &q3, 1e-8);
    vector<int> q(n);
    cout << "n: " << n << endl;
    int a = 0;
    for( int i = 0; i < 1; ++i ) {
        q[i] = i;
        //int a = i;
        cout << i << endl;
        opt.add_equality_constraint(myvconstraint, &a, 1e-8);
    }*/

    opt.set_xtol_rel(1e-4);

    double minf = 0;
    nlopt::result result = opt.optimize(x, minf);

	// actually perform the optimization
	if( result < 0 )
        printf("nlopt failed!\n");
    else {
        printf("found minimum after %d evaluations\n", counter);
        for( int i = 0; i < x.size(); ++i )
            cout << x[i] << endl;
        printf("found minimum at %0.10g\n", minf);
    }
    ofstream ofs("point_solve.rect");
    if( ofs ) {
        for( int i = 0; i < x.size(); ++i )
            ofs << x[i] << endl;
    }



	return 0;
}
