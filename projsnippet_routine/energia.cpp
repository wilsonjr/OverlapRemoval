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

using namespace std;

int counter = 0;

vector<double> height;
vector<double> width;
vector<vector<double> > L;
vector<double> originais;
vector<double> orig_x;
vector<double> orig_y;
double alpha;

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

double objective_function(const std::vector<double> &x, std::vector<double> &grad, void *my_func_data)
{
    counter++;
   // cout << "counter: " << counter << endl;
    int N = x.size()-1;
    int n = N/2;


    if (!grad.empty()) {
      //  cout << "counter: " << counter << endl;
        double q = 0;
        for( int i = 0; i < grad.size(); ++i ) {
            grad[i] = 0;
        }
        //cout << "-----------------------------------------------" << endl;
        for( int i = 0; i < N; i += 2 )
            for( int j = i+2; j < N; j += 2 ) {

               // grad[i] += d_O_dx(x[i], width[i/2], x[j], width[j/2], -1) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                if( x[i] >= x[j] ) {
                    q = pow(width[j/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[i] += (1.-alpha)*(2./(n*(n-1.)))*((-4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[j/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                } else {
                    q = pow(width[i/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[i] += (1.-alpha)*(2./(n*(n-1.)))*((-4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[i/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                }

               // grad[i+1] += d_O_dx(x[i+1], height[i/2], x[j+1], height[j/2], -1) * O(x[i], width[i/2], x[j], width[j/2]);
                if( x[i+1] >= x[j+1] ) {
                    q = pow(height[j/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[i+1] += (1.-alpha)*(2./(n*(n-1.)))*((-4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[j/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                } else {
                    q = pow(height[i/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[i+1] += (1.-alpha)*(2./(n*(n-1.)))*((-4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[i/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                }

               // grad[j] += d_O_dx(x[i], width[i/2], x[j], width[j/2], 1) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                if( x[i] >= x[j] ) {
                    q = pow(width[j/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[j] += (1.-alpha)*(2./(n*(n-1.)))*((4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[j/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                } else {
                    q = pow(width[i/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[j] += (1.-alpha)*(2./(n*(n-1.)))*((4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[i/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                }

              //  grad[j+1] += d_O_dx(x[i+1], height[i/2], x[j+1], height[j/2], 1) * O(x[i], width[i/2], x[j], width[j/2]);
                if( x[i+1] >= x[j+1] ) {
                    q = pow(height[j/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[j+1] += (1.-alpha)*(2./(n*(n-1.)))*((4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[j/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                } else {
                    q = pow(height[i/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[j+1] += (1.-alpha)*(2./(n*(n-1.)))*((4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[i/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                }
            }


        double w = x[N];
        vector<double> pontos_o_x;
        vector<double> pontos_o_y;
        for( int i = 0; i < N; i += 2 ) {
            pontos_o_x.push_back(x[i]);
            pontos_o_y.push_back(x[i+1]);
        }

        vector<double> X = Lxy(L, pontos_o_x);
        vector<double> Y = Lxy(L, pontos_o_y);
        vector<double> deltax = delta_x();//Lxy(L, orig_x);
        vector<double> deltay = delta_y();// = Lxy(L, orig_y);
        for( int i = 0; i < X.size(); ++i ) {
            X[i] = X[i] - w*deltax[i];
            Y[i] = Y[i] - w*deltay[i];
        }

        double dx = norma(deltax);
        double dy = norma(deltay);

        double first = pow((double)n, 2.0) / (2.0*(pow(dx, 2.0)+pow(dy, 2.0)));
        for( int i = 0; i < N; i += 2 ) {
            for( int j = 0; j < X.size(); ++j )
                grad[i] += alpha*first*(2.0*L[j][i/2]*X[j]);
            for( int j = 0; j < Y.size(); ++j )
                grad[i+1] += alpha*first*(2.0*L[j][i/2]*Y[j]);
        }
        for( int i = 0; i < X.size(); ++i )
            grad[N] += alpha*first*(-2.0*deltax[i]*X[i]);
        for( int i = 0; i < Y.size(); ++i )
            grad[N] += alpha*first*(-2.0*deltay[i]*Y[i]);


    }

    double energia_o = 0.0;
    for( int i = 0; i < N; i += 2 )
        for( int j = i+2; j < N; j += 2 ) {
            energia_o += O(x[i], width[i/2], x[j], width[j/2]) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
        }

    energia_o = (2.0/(n*(n-1.0)))*energia_o;

    double energia_n = e_n(x);
    //cout << "energia_o: " << energia_o << ", energia_n: " << energia_n << endl;
    //cout << "energia total: " << (1.-alpha)*energia_o + alpha*energia_n << endl;
    return (1.-alpha)*energia_o + alpha*energia_n;
}

vector<double> read_elems()
{
    double x = 0.0, y = 0.0, w = 0.0, h = 0.0;
    vector<double> elems;
    ifstream ifs("projsnippet_routine/points.rect");

    if( ifs ) {
        int qtd = 0;
        ifs >> qtd;
        for( int i = 0; i < qtd; ++i ) {
            ifs >> x >> y >> w >> h;
            elems.push_back(x);
            elems.push_back(y);
            width.push_back(w);
            height.push_back(h);

            orig_x.push_back(x);
            orig_y.push_back(y);
        }
        ifs >> x;
        x = qtd/5;
        elems.push_back(x);
        ifs >> alpha;

        originais = vector<double>(elems.begin(), elems.end());
        ifs.close();
    }

    return elems;
}

vector<vector<double> > read_matrix() {

    vector<vector<double> > m;

    ifstream ifs("projsnippet_routine/matrixL.matrix");

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
    //cout << media*(n/2)+(menor+30) << endl;
    //cout << media << endl;
    //cout << media*(n/2) << endl;
    //cout << (menor+30) << endl;

    vector<double> up(n, media*(n/2)+(menor+30));
    opt.set_lower_bounds(lb);
    opt.set_upper_bounds(up);
    opt.set_stopval(0.0009);
    opt.set_maxeval(5000);
    opt.set_maxtime(900);
    opt.set_min_objective(objective_function, NULL);

    double minf = 0;
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





	return 0;
}


