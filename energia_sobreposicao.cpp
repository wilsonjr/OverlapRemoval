#include <iostream>
#include <cstdio>
#include <vector>
#include <cmath>
#include <fstream>
#include <nlopt.hpp>

using namespace std;

int counter = 0;

vector<double> height;
vector<double> width;

inline double x_plus(double x)
{
	if( x < 0.0 ) {
		return 0.0;
    }
	return x;
}

inline double d_xplus_dx(double x)
{
    if( x < 0.0 )
        return 0;
    return 1;
}

inline double O(double ai, double bi, double aj, double bj)
{
	if( ai >= aj )
		return (1.0 / pow(bj, 4.0)) * pow(x_plus(pow(bj, 2) - pow(ai-aj, 2)), 2);
	return (1.0 / pow(bi, 4.0)) * pow(x_plus(pow(bi, 2) - pow(ai-aj, 2)), 2);
}

inline double d_O_dx(double ai, double bi, double aj, double bj, double signal)
{
    if( ai >= aj ) {
        double q = (bj, 2.) - pow(ai-aj, 2.);
        return (signal*4. * (ai-aj) * x_plus(q) * d_xplus_dx(q) ) / pow(bj, 4.);
    }
    double q = (bi, 2.) - pow(ai-aj, 2.);
    return (signal*4. * (ai-aj) * x_plus(q) * d_xplus_dx(q) ) / pow(bi, 4.);
}

double objective_function(const std::vector<double> &x, std::vector<double> &grad, void *my_func_data)
{
    counter++;
    int n = x.size()/2;
    int N = x.size();

    if (!grad.empty()) {
        double q = 0;

        for( int i = 0; i < N; i += 2 )
            for( int j = i+2; j < N; j += 2 ) {

                //grad[i] += d_O_dx(x[i], width[i/2], x[j], width[j/2], -1) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                if( x[i] >= x[j] ) {
                    q = pow(width[j/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[i] += (-4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[j/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                } else {
                    q = pow(width[i/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[i] += (-4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[i/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                }

                //grad[i+1] += d_O_dx(x[i+1], height[i/2], x[j+1], height[j/2], -1) * O(x[i], width[i/2], x[j], width[j/2]);
                if( x[i+1] >= x[j+1] ) {
                    q = pow(height[j/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[i+1] += (-4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[j/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]);
                } else {
                    q = pow(height[i/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[i+1] += (-4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[i/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]);
                }

                //grad[j] += d_O_dx(x[i], width[i/2], x[j], width[j/2], 1) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                if( x[i] >= x[j] ) {
                    q = pow(width[j/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[j] += (4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[j/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                } else {
                    q = pow(width[i/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[j] += (4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[i/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                }

             //   grad[j+1] += d_O_dx(x[i+1], height[i/2], x[j+1], height[j/2], 1) * O(x[i], width[i/2], x[j], width[j/2]);
                if( x[i+1] >= x[j+1] ) {
                    q = pow(height[j/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[j+1] += (4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[j/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]);
                } else {
                    q = pow(height[i/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[j+1] += (4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[i/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]);
                }

            }

        for( int i = 0; i < N; ++i )
            grad[i] *= (2./(n*(n-1.)));
    }

    double sum = 0.0;
    for( int i = 0; i < N; i += 2 )
        for( int j = i+2; j < N; j += 2 )
            sum += O(x[i], width[i/2], x[j], width[j/2]) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
    sum *= (2.0/(n*(n-1)));

    return sum;
}

vector<double> read_elems()
{
    double x = 0.0, y = 0.0, w = 0.0, h = 0.0;
    vector<double> elems;
    ifstream ifs("points.rect");
    if( ifs ) {
        while( ifs >> x >> y >> w >> h) {
            elems.push_back(x);
            elems.push_back(y);
            width.push_back(w);
            height.push_back(h);
        }
    }

    return elems;
}

int main() {
    vector<double> x = read_elems();
    int n = x.size();

	nlopt::opt opt(nlopt::LD_MMA, n);
    vector<double> lb(n, 0);
    vector<double> up(n, 800);
    opt.set_lower_bounds(lb);
    opt.set_upper_bounds(up);

    opt.set_min_objective(objective_function, NULL);

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

