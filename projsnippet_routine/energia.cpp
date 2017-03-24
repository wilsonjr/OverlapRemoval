#include <iostream>
#include <cmath>
#include <cstdio>
#include <limits>
#include <algorithm>
#include <vector>
#include <cmath>
#include <list>
#include <set>
#include <iomanip>
#include <fstream>
#include <nlopt.hpp>
#include <chrono>
#include <numeric>

//#define DEBUG

#define X_PLUS(x) (x < 0.0 ? 0 : x)
#define D_XPLUS_DX(x) (x < 0.0 ? 0 : 1)

using namespace std;

vector<double> height;
vector<double> width;
vector<vector<double> > L;
vector<double> orig_x, deltx;
vector<double> orig_y, delty;
double alpha;
double test_w = 5;

vector<double> Lxy(const vector<vector<double> >& l, const vector<double>& xy)
{
    vector<double> delta_xy = vector<double>(xy.size(), 0);

    for( int i = 0; i < l.size(); ++i ) {
        for( int j = 0; j < l.size(); ++j ) {
            delta_xy[i] += l[i][j]*xy[j];
        }
    }
    return delta_xy;

}

inline double norm(const vector<double>& vet)
{
    return sqrt(std::inner_product(vet.begin(), vet.end(), vet.begin(), 0.0));
}

inline vector<double> delta_x()
{
    return deltx;
}

inline vector<double> delta_y()
{
    return delty;
}

double O(double ai, double bi, double aj, double bj)
{
	if( ai >= aj )
		return (1.0 / pow(bj, 4.0)) * pow(X_PLUS(pow(bj, 2.0) - pow(ai-aj, 2.0)), 2.0);
	return (1.0 / pow(bi, 4.0)) * pow(X_PLUS(pow(bi, 2.0) - pow(ai-aj, 2.0)), 2.0);
}

double d_O_dx(double ai, double bi, double aj, double bj, double signal)
{
    if( ai >= aj ) {
        double q = (bj, 2.) - pow(ai-aj, 2.);
        return (signal*4. * (ai-aj) * X_PLUS(q) * D_XPLUS_DX(q) ) / pow(bj, 4.);
    }
    double q = (bi, 2.) - pow(ai-aj, 2.);
    return (signal*4. * (ai-aj) * X_PLUS(q) * D_XPLUS_DX(q) ) / pow(bi, 4.);
}


void get_elements(const vector<double>& elems, vector<double>& xx, vector<double>& yy, double& w)
{
    int n = elems.size()-1;

    for( int i = 0; i < n; i += 2 ) {
        xx.push_back(elems[i]);
        yy.push_back(elems[i+1]);
    }

    w = elems[n];
}

double fn(const vector<double>& X, const vector<double>& Y, const double& w)
{
    int n = X.size();
    double soma_eo = 0, soma_en = 0;

    /***** Calculate Eo **********/

    for( int i = 0; i < n; ++i )
        for( int j = i+1; j < n; ++j )
            soma_eo += O(X[i], width[i], X[j], width[j])*O(Y[i], height[i], Y[j], height[j]);
    soma_eo = (2./(n*(n-1.)))*soma_eo;


    /***** Calculate En **********/
    vector<double> dx = delta_x();
    vector<double> dy = delta_y();

    vector<double> Lx = Lxy(L, X);
    vector<double> Ly = Lxy(L, Y);

    for( int i = 0; i < n; ++i ) {
        Lx[i] = Lx[i] - w*dx[i];
        Ly[i] = Ly[i] - w*dy[i];
    }

    double norm_Lxwdx = pow(norm(Lx), 2);
    double norm_Lywdy = pow(norm(Ly), 2);
    double soma_diff = norm_Lxwdx+norm_Lywdy;

    soma_en = ((n*n)/(2.*(pow(norm(dx), 2) + pow(norm(dy), 2))))  * soma_diff;

    return (1.-alpha)*soma_eo + alpha*soma_en;
}

double objective_function(const std::vector<double> &x, std::vector<double> &grad, void *my_func_data)
{
    int N = x.size()-1;
    int n = N/2;

    if (!grad.empty()) {
        double q = 0, multiplier = (1.-alpha)*(2./(n*(n-1.)));
        grad.assign(grad.size(), 0.0);

        for( int i = 0; i < N; i += 2 )
            for( int j = i+2; j < N; j += 2 ) {

                if( x[i] >= x[j] ) {
                    q = pow(width[j/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[i] += multiplier*((-4. * (x[i]-x[j]) * X_PLUS(q) * D_XPLUS_DX(q)) / pow(width[j/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                } else {
                    q = pow(width[i/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[i] += multiplier*((-4. * (x[i]-x[j]) * X_PLUS(q) * D_XPLUS_DX(q)) / pow(width[i/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                }

                if( x[i+1] >= x[j+1] ) {
                    q = pow(height[j/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[i+1] += multiplier*((-4. * (x[i+1]-x[j+1]) * X_PLUS(q) * D_XPLUS_DX(q)) / pow(height[j/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                } else {
                    q = pow(height[i/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[i+1] += multiplier*((-4. * (x[i+1]-x[j+1]) * X_PLUS(q) * D_XPLUS_DX(q)) / pow(height[i/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                }

                if( x[i] >= x[j] ) {
                    q = pow(width[j/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[j] += multiplier*((4. * (x[i]-x[j]) * X_PLUS(q) * D_XPLUS_DX(q)) / pow(width[j/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                } else {
                    q = pow(width[i/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[j] += multiplier*((4. * (x[i]-x[j]) * X_PLUS(q) * D_XPLUS_DX(q)) / pow(width[i/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                }

                if( x[i+1] >= x[j+1] ) {
                    q = pow(height[j/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[j+1] += multiplier*((4. * (x[i+1]-x[j+1]) * X_PLUS(q) * D_XPLUS_DX(q)) / pow(height[j/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                } else {
                    q = pow(height[i/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[j+1] += multiplier*((4. * (x[i+1]-x[j+1]) * X_PLUS(q) * D_XPLUS_DX(q)) / pow(height[i/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                }
            }


        double w = x[N];
        vector<double> pontos_o_x;
        vector<double> pontos_o_y;
        for( int i = 0; i < N; i += 2 ) {
            pontos_o_x.push_back(x[i]);
            pontos_o_y.push_back(x[i+1]);
        }

        vector<double> dx = delta_x();
        vector<double> dy = delta_y();

        vector<double> Lx = Lxy(L, pontos_o_x);
        vector<double> Ly = Lxy(L, pontos_o_y);

        for( int i = 0; i < n; ++i ) {
            Lx[i] = Lx[i] - w*dx[i];
            Ly[i] = Ly[i] - w*dy[i];
        }


        multiplier = alpha*pow((double)n, 2.0) / (2.0*(pow(norm(dx), 2.0)+pow(norm(dy), 2.0)));
        for( int i = 0; i < N; i += 2 ) {
            for( int j = 0; j < Lx.size(); ++j )
                grad[i] += multiplier*(2.0*L[j][i/2]*Lx[j]);
            for( int j = 0; j < Ly.size(); ++j )
                grad[i+1] += multiplier*(2.0*L[j][i/2]*Ly[j]);
        }
        for( int i = 0; i < Lx.size(); ++i )
            grad[N] += multiplier*(-2.0*dx[i]*Lx[i]);
        for( int i = 0; i < Ly.size(); ++i )
            grad[N] += multiplier*(-2.0*dy[i]*Ly[i]);


    }

    vector<double> X, Y;
    double W;
    get_elements(x, X, Y, W);
    #ifdef DEBUG
        double r = fn(X, Y, W);
        cout << "energia: " << r << endl;
    #endif // DEBUG
    return fn(X, Y, W);
}

vector<double> read_elems()
{
    double x = 0.0, y = 0.0, w = 0.0, h = 0.0;
    vector<double> elems;
    #ifdef DEBUG
        ifstream ifs("points.rect");
    #endif // DEBUG

    #ifndef DEBUG
        ifstream ifs("projsnippet_routine/points.rect");
    #endif // DEBUG


    if( ifs ) {
        int qtd = 0;
        ifs >> qtd;
        for( int i = 0; i < qtd; ++i ) {
            ifs >> x >> y >> w >> h;
            elems.push_back(test_w*x);
            elems.push_back(test_w*y);
            width.push_back(w);
            height.push_back(h);

            orig_x.push_back(x);
            orig_y.push_back(y);
        }
        ifs >> x;
        x = test_w;
        elems.push_back(x);
        ifs >> alpha;

        ifs.close();
    }

    return elems;
}

vector<vector<double> > read_matrix() {

    vector<vector<double> > m;

    #ifdef DEBUG
        ifstream ifs("matrixL.matrix");
    #endif // DEBUG

    #ifndef DEBUG
        ifstream ifs("projsnippet_routine/matrixL.matrix");
    #endif // DEBUG

    if( ifs ) {
        int dim;
        ifs >> dim;
        m = vector<vector<double> >(dim, vector<double>(dim, 0));

        for( int i = 0; i < dim; ++i )
            for( int j = 0; j < dim; ++j )
                ifs >> m[i][j];
    }

    deltx = Lxy(m, orig_x);
    delty = Lxy(m, orig_y);


    return m;
}

int main(int argc, char** argv) {
    vector<double> x = read_elems();
    L = read_matrix();
    int n = x.size();

    double menor = x[0];
    for( int i = 1; i < x.size()-1; ++i )
        menor = min(menor, x[i]);


    double media = 0;
    for( int i = 0; i < width.size(); ++i )
        media += (width[i]+height[i]);
    media /= (2.0*width.size());

    nlopt::opt opt(nlopt::LD_MMA, n);
    vector<double> lb(n, 0);
    vector<double> up(n, test_w*(media*(n/2)+(menor+30)));
    opt.set_lower_bounds(lb);
    opt.set_upper_bounds(up);
    opt.set_stopval(0.00001);
    opt.set_maxeval(5000);
    opt.set_maxtime(900);
    opt.set_ftol_abs(0.00001);
    opt.set_ftol_rel(0.00001);
    opt.set_min_objective(objective_function, NULL);

    double minf = 0;

    #ifdef DEBUG
        std::chrono::steady_clock::time_point begin_time = std::chrono::steady_clock::now();
        nlopt::result result = opt.optimize(x, minf);
        std::chrono::steady_clock::time_point end_time = std::chrono::steady_clock::now();
        std::cout << "Time (s):" << std::chrono::duration_cast<std::chrono::nanoseconds>(end_time-begin_time).count()/1.0e9 << std::endl;

        ofstream ofs("point_solve.rect");
        if( ofs ) {
            ofs << x.size()/2 << endl;
            for( int i = 0; i < x.size()-1; i+=2 ) {
                ofs << x[i] << " " << x[i+1] << " " << height[i/2] << " " << width[i/2] << endl;
            }
            ofs << x[x.size()-1];
            ofs.close();
        }
    #endif // DEBUG

    #ifndef DEBUG
        nlopt::result result = opt.optimize(x, minf);

        ofstream ofs("projsnippet_routine/point_solve.rect");
        if( ofs ) {
            ofs << x.size()/2 << endl;
            for( int i = 0; i < x.size()-1; i+=2 ) {
                ofs << x[i] << " " << x[i+1] << " " << height[i/2] << " " << width[i/2] << endl;
            }
            ofs << x[x.size()-1];
            ofs.close();
        }
    #endif // DEBUG

    return 0;
}

