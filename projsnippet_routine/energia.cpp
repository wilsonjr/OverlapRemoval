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

using namespace std;

#define DEBUG
#define DEBUG_TIME

typedef struct point {
    double dist;
    int id;
    point() {}
    point(int i, double d):id(i), dist(d) {}
    bool operator<(const point& b) const {
        return dist < b.dist;
    }
} point;


typedef struct vertice {
    long id;
    bool visitado;
    double x, y;
    list<long> adj;
    set<point> elems;
} vertice;


typedef struct {
    int idx;
} my_constraint_data;
int counter = 0;

vector<double> height;
vector<double> width;
vector<vector<double> > L;
vector<double> originais;
vector<double> orig_x, X0;
vector<double> orig_y, Y0;
double alpha;
double h, v;

int idx_maior;

int findMultiplier(const vector<double>& c, double w)
{
    for( int i = 1; i <= c.size(); ++i ) {
        int z = i;
        if( z <= 2 )
            z = 2;

        bool menorIgual = false;
        bool var = true;
        for( int j = 1, k = 1; j <= c.size(); ++j ) {

            if( i == j )
                menorIgual = true;
            if( menorIgual )
                var = var && (w*c[k] - w*c[z] <= 0);
            else
                var = var && (w*c[k] - w*c[z] > 0);

            if( k < i && k <= j )
                k++;
            if( z <= j+1 )
                z++;

        }
        if( var )
            return i-1;
    }

    return c.size()-1;
}

vector<double> Lxy(vector<vector<double> >& l, vector<double>& xy)
{
    vector<double> delta_xy = vector<double>(xy.size(), 0);

    for( int i = 0; i < l.size(); ++i ) {
        for( int j = 0; j < l.size(); ++j ) {
            delta_xy[i] += l[i][j]*xy[j];
        }
    }
    return delta_xy;

}

inline double norma(const vector<double>& c)
{
    double soma = 0.0;
    for( int i = 0; i < c.size(); ++i )
        soma += (c[i]*c[i]);
    return sqrt(soma);
}

vector<double> delta_x()
{
    return Lxy(L, orig_x);
}

vector<double> delta_y()
{
    return Lxy(L, orig_y);
}

double e_n(const vector<double>& pontos_o)
{
    int N = pontos_o.size()-1;
    int n = N/2;
    double w = pontos_o[N];
    vector<double> pontos_o_x;
    vector<double> pontos_o_y;

    for( int i = 0; i < N; i += 2 ) {
        pontos_o_x.push_back(pontos_o[i]);
        pontos_o_y.push_back(pontos_o[i+1]);
    }

    vector<double> x = Lxy(L, pontos_o_x);
    vector<double> y = Lxy(L, pontos_o_y);
    vector<double> deltax = delta_x();//Lxy(L, orig_x);
    vector<double> deltay = delta_y();// = Lxy(L, orig_y);
    for( int i = 0; i < x.size(); ++i ) {
        x[i] = x[i] - w*deltax[i];
        y[i] = y[i] - w*deltay[i];
    }

    double dx = pow(norma(deltax), 2.0);
    double dy = pow(norma(deltay), 2.0);
    double ddx = pow(norma(x), 2.0);
    double ddy = pow(norma(y), 2.0);

    return (pow((double)n, 2.0)* (ddx + ddy)) /
                    (2.0 * (dx + dy));
}

inline double distancia(double x1, double y1, double x2, double y2)
{
    return sqrt(pow(x1-x2, 2.) + pow(y1-y2, 2.));
}

bool comp_distancia(const point& a, const point& b)
{
    return a.dist < b.dist;
}

double x_plus(double x)
{
	if( x < 0.0 )
		return 0.0;

	return x;
}

double d_xplus_dx(double x)
{
    if( x < 0.0 )
        return 0;
    return 1;
}

double O(double ai, double bi, double aj, double bj)
{
	if( ai >= aj )
		return (1.0 / pow(bj, 4.0)) * pow(x_plus(pow(bj, 2.0) - pow(ai-aj, 2.0)), 2.0);
	return (1.0 / pow(bi, 4.0)) * pow(x_plus(pow(bi, 2.0) - pow(ai-aj, 2.0)), 2.0);
}

double d_O_dx(double ai, double bi, double aj, double bj, double signal)
{
    if( ai >= aj ) {
        double q = (bj, 2.) - pow(ai-aj, 2.);
        return (signal*4. * (ai-aj) * x_plus(q) * d_xplus_dx(q) ) / pow(bj, 4.);
    }
    double q = (bi, 2.) - pow(ai-aj, 2.);
    return (signal*4. * (ai-aj) * x_plus(q) * d_xplus_dx(q) ) / pow(bi, 4.);
}

void transfer_elements(const std::vector<double> &x, std::vector<double>& X, std::vector<double>& Y)
{
    int N = x.size()-1;
    for( int i = 0; i < N; i += 2 ) {
        X.push_back(x[i]);
        Y.push_back(x[i+1]);
    }
}

void update_gradient(std::vector<double> &grad, std::vector<double>& grad_x, std::vector<double>& grad_y)
{
    int N = grad.size()-1;
    for( int i = 0, j = 0; i < N; i += 2, j++ ) {
        grad[i] += grad_x[j];
        grad[i+1] += grad_y[j];
    }


}

double sign(double element)
{
    if( element == 0.0 ) return 0;
    if( element > 0.0 ) return 1;
    return -1;
}

double objective_function(const std::vector<double> &x, std::vector<double> &grad, void *my_func_data)
{
    counter++;
   // cout << "counter: " << counter << endl;
    int N = x.size()-1;
    int n = N/2;

    vector<double> X, Y;
    double w = x[N];
    transfer_elements(x, X, Y);

    if (!grad.empty()) {
      //  cout << "counter: " << counter << endl;
        for( int i = 0; i < grad.size(); ++i ) {
            grad[i] = 0;
        }
        vector<double> grad_en_x(X.size(), 0), grad_en_y(Y.size(), 0), grad_eo_x(X.size(), 0), grad_eo_y(Y.size(), 0);

        double h4v4 = pow(h, 4.)*pow(v, 4.);
        double fator_on = 2.0/(n*(n-1.));
        double n2 = n*n;

        // gradient for EO component (x)
        for( int i = 0; i < X.size(); ++i ) {
            double soma = 0;

            for( int j = 0; j < X.size(); ++j ) {
                if( i == j )
                    continue;

                double valuex = x_plus(pow(h, 2.) - pow(X[i] - X[j], 2.));
                double valuey = x_plus(pow(v, 2.) - pow(Y[i] - Y[j], 2.));

                if( !valuex || !valuey )
                    continue;

                if( i < j ) {
                    double num = 2. * (2.*X[i] - 2.*X[j]) * (pow(X[i] - X[j], 2.) - h*h) * pow(pow(Y[i] - Y[j], 2.) - v*v, 2.);
                    soma += (num/h4v4);
                } else {
                    double num = - 2. * (2.*X[j] - 2.*X[i]) * (pow(X[j] - X[i], 2.) - h*h) * pow(pow(Y[j] - Y[i], 2.) - v*v, 2.);
                    soma += (num/h4v4);
                }
            }
            grad_eo_x[i] = soma*fator_on*(1-alpha);
        }

        // gradient for EO component (y)
        for( int i = 0; i < Y.size(); ++i ) {
            double soma = 0;

            for( int j = 0; j < Y.size(); ++j ) {
                if( i == j )
                    continue;

                double valuex = x_plus(pow(h, 2.) - pow(X[i] - X[j], 2.));
                double valuey = x_plus(pow(v, 2.) - pow(Y[i] - Y[j], 2.));

                if( valuex == 0.0 || valuey == 0.0 )
                    continue;

                if( i < j ) {
                    double num = 2. * (2.*Y[i] - 2.*Y[j]) * (pow(Y[i] - Y[j], 2.) - v*v) * pow(pow(X[i] - X[j], 2.) - h*h, 2.);
                    soma += (num/h4v4);
                } else {
                    double num = - 2. * (2.*Y[j] - 2.*Y[i]) * (pow(Y[j] - Y[i], 2.) - v*v) * pow(pow(Y[j] - Y[i], 2.) - h*h, 2.);
                    soma += (num/h4v4);
                }
            }
            grad_eo_y[i] = soma*fator_on*(1-alpha);
        }

        // -----------------------------------------------------------------------------------------------------------------------
        double divisor = 0;
        for( int i = 0; i < n; ++i ) {
            double somax = 0, somay = 0;
            for( int j = 0; j < n; ++j ) {
                somax += L[j][i]*X0[j];
                somay += L[j][i]*Y0[j];
            }
            divisor += (2.*pow(fabs(somax), 2.) + 2.*pow(fabs(somay), 2.));
        }


        // gradient for EN component (x)
        for( int i = 0; i < n; ++i ) {
            double soma = 0;
            for( int j = 0; j < n; ++j ) {
                double valuex = 0, valuex0 = 0;
                for( int k = 0; k < n; ++k ) {
                    valuex += L[k][j]*X[k];
                    valuex0 += L[k][j]*X0[k];
                }

                double value = fabs(valuex - w*valuex0);
                double value_sign = sign(valuex - w*valuex0);

                soma += (2.*L[i][j]*value*value_sign);
            }
            grad_en_x[i] = ((n2*alpha*soma)/divisor);
        }

        // gradient for EN component (y)
        for( int i = 0; i < n; ++i ) {
            double soma = 0;
            for( int j = 0; j < n; ++j ) {
                double valuey = 0, valuey0 = 0;
                for( int k = 0; k < n; ++k ) {
                    valuey += L[k][j]*Y[k];
                    valuey0 += L[k][j]*Y0[k];
                }

                double value = fabs(valuey - w*valuey0);
                double value_sign = sign(valuey - w*valuey0);

                soma += (2.*L[i][j]*value*value_sign);
            }
            grad_en_y[i] = ((n2*alpha*soma)/divisor);
        }

        update_gradient(grad, grad_eo_x, grad_eo_y);
        update_gradient(grad, grad_en_x, grad_en_y);


    }

    double energia_o = 0.0;

    for( int i = 0; i < X.size(); ++i )
        for( int j = i+1; j < X.size(); ++j )
            energia_o += O(X[i], h, X[j], h) * O(Y[i], v, Y[j], v);
    /*for( int i = 0; i < N; i += 2 )
        for( int j = i+2; j < N; j += 2 ) {
            energia_o += O(x[i], h, x[j], h) * O(x[i+1], v, x[j+1], v);
        }*/

    energia_o = (2.0/(n*(n-1.0)))*energia_o;

    //double energia_n = e_n(x);
    double energia_n = 0;
    #ifdef DEBUG
        cout << "O-energy: " << energia_o << ", N-energy: " << energia_n << endl;
    #endif // DEBUG


    return energia_o;
    //return energia_n;
    //return (1.-alpha)*energia_o + alpha*energia_n;
}

vector<double> read_elems()
{
    double x = 0.0, y = 0.0, ww = 0.0, hh = 0.0;
    vector<double> elems;

    #ifdef DEBUG
        ifstream ifs("points.rect");
    #else
        ifstream ifs("projsnippet_routine/points.rect");
    #endif // DEBUG

    if( ifs ) {
        int qtd = 0;
        ifs >> qtd;
        for( int i = 0; i < qtd; ++i ) {
            ifs >> x >> y >> ww >> hh;
            elems.push_back(x);
            elems.push_back(y);

            width.push_back(ww);
            height.push_back(hh);

            orig_x.push_back(x);
            orig_y.push_back(y);
            X0.push_back(x);
            Y0.push_back(y);

        }
        h = width[0];
        v = width[0];

        ifs >> x;
        x = 1;//td/5;
        elems.push_back(x);
        ifs >> alpha;

        originais = vector<double>(elems.begin(), elems.end());
        ifs.close();
    }
  //  alpha = 0.8;
    return elems;
}

vector<vector<double> > read_matrix() {

    vector<vector<double> > m;

    #ifdef DEBUG
        ifstream ifs("matrixL.matrix");
    #else
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

    vector<double> up(n, h*((n-1)/2));
    opt.set_lower_bounds(lb);
    opt.set_upper_bounds(up);
    opt.set_stopval(0.00001);
  //  opt.set_maxeval(5000);
   // opt.set_maxtime(900);
    opt.set_min_objective(objective_function, NULL);

    double minf = 0;
    std::chrono::steady_clock::time_point begin_time = std::chrono::steady_clock::now();
    nlopt::result result = opt.optimize(x, minf);
    std::chrono::steady_clock::time_point end_time = std::chrono::steady_clock::now();

    #ifdef DEBUG_TIME
        std::cout << "Time: " << std::chrono::duration_cast<std::chrono::microseconds>(end_time-begin_time).count()/1000000.0 << std::endl;
    #endif


    ofstream ofs("projsnippet_routine/point_solve.rect");
    if( ofs ) {
        ofs << x.size()/2 << endl;
        for( int i = 0; i < x.size()-1; i+=2 ) {
            ofs << x[i] << " " << x[i+1] << " " << height[i/2] << " " << width[i/2] << endl;
        }
        ofs << x[x.size()-1];
        ofs.close();
    }

	return 0;
}


