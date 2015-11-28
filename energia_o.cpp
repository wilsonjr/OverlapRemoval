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


int counter = 0;

vector<double> height;
vector<double> width;
vector<vector<double> > L;
vector<double> originais;
vector<double> orig_x;
vector<double> orig_y;
vector<double> valencia;
double alpha;

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
    double maior = pow(c[0], 2.);
    for( int i = 1; i < c.size(); ++i )
        //if( maior < c[i] )
            maior += pow(c[i], 2.);

    return sqrt(maior);
}
/*
double delta(const vector<double>& pontos, int idx, int add)
{
    int N = pontos.size()-1;
    double soma = 0.0;
    double n = 0.0;

    for( int i = 0; i < N/2; ++i ) {
        if( L[idx][i] != 0 && idx != i ) {
            soma += pontos[i*2 + add];
            n++;
        }
    }

    return pontos[idx*2 + add] - (1.0/n)*soma;
}
*/
vector<double> delta_x()
{
    /*
    vector<double> dx(orig_x.size(), 0 );

    for( int i = 0; i < L.size(); ++i ) {
        for( int j = 0; j < L[i].size(); ++j )
            if( L[i][j] != 0 )
                dx[i] += orig_x[j];
        dx[i] = orig_x[i] - dx[i]/valencia[i];
    }

    return dx;*/
    return Lxy(L, orig_x);
}

vector<double> delta_y()
{
    /*
    vector<double> dy(orig_y.size(), 0 );

    for( int i = 0; i < L.size(); ++i ) {
        for( int j = 0; j < L[i].size(); ++j )
            if( L[i][j] != 0 )
                dy[i] += orig_y[j];

        dy[i] = orig_y[i] - dy[i]/valencia[i];
    }

    return dy;*/
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

    double dx = pow(norma(deltax), 2);
    double dy = pow(norma(deltay), 2);
    double ddx = pow(norma(x), 2);
    double ddy = pow(norma(y), 2);

    return ((n * n) / (2 * (dx + dy))) * (ddx + ddy);
}

inline double distancia(double x1, double y1, double x2, double y2)
{
    return sqrt(pow(x1-x2, 2.) + pow(y1-y2, 2.));
}

bool comp_distancia(const point& a, const point& b)
{
    return a.dist < b.dist;
}

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
    int N = x.size()-1;
    int n = N/2;


    if (!grad.empty()) {
        double q = 0;
    vector<double> grad2(grad.size(), 0);
        for( int i = 0; i < grad.size(); ++i )
            grad[i] = 0;


        for( int i = 0; i < N; i += 2 )
            for( int j = i+2; j < N; j += 2 ) {

                //grad[i] += d_O_dx(x[i], width[i/2], x[j], width[j/2], -1) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                if( x[i] >= x[j] ) {
                    q = pow(width[j/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[i] += (2./(n*(n-1.)))*((-4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[j/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                } else {
                    q = pow(width[i/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[i] += (2./(n*(n-1.)))*((-4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[i/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                }

                //grad[i+1] += d_O_dx(x[i+1], height[i/2], x[j+1], height[j/2], -1) * O(x[i], width[i/2], x[j], width[j/2]);
                if( x[i+1] >= x[j+1] ) {
                    q = pow(height[j/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[i+1] += (2./(n*(n-1.)))*((-4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[j/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                } else {
                    q = pow(height[i/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[i+1] += (2./(n*(n-1.)))*((-4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[i/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                }

                //grad[j] += d_O_dx(x[i], width[i/2], x[j], width[j/2], 1) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
                if( x[i] >= x[j] ) {
                    q = pow(width[j/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[j] += (2./(n*(n-1.)))*((4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[j/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                } else {
                    q = pow(width[i/2], 2.) - pow(x[i]-x[j], 2.);
                    grad[j] += (2./(n*(n-1.)))*((4. * (x[i]-x[j]) * x_plus(q) * d_xplus_dx(q)) / pow(width[i/2], 4.) * O(x[i+1], height[i/2], x[j+1], height[j/2]));
                }

             //   grad[j+1] += d_O_dx(x[i+1], height[i/2], x[j+1], height[j/2], 1) * O(x[i], width[i/2], x[j], width[j/2]);
                if( x[i+1] >= x[j+1] ) {
                    q = pow(height[j/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[j+1] += (2./(n*(n-1.)))*((4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[j/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                } else {
                    q = pow(height[i/2], 2.) - pow(x[i+1]-x[j+1], 2.);
                    grad[j+1] += (2./(n*(n-1.)))*((4. * (x[i+1]-x[j+1]) * x_plus(q) * d_xplus_dx(q)) / pow(height[i/2], 4.) * O(x[i], width[i/2], x[j], width[j/2]));
                }
            }
        for( int i = 0; i < grad.size(); ++i )
            grad[i] *= (1-alpha);
    }

    double energia_o = 0.0;
    for( int i = 0; i < N; i += 2 )
        for( int j = i+2; j < N; j += 2 )
            energia_o += O(x[i], width[i/2], x[j], width[j/2]) * O(x[i+1], height[i/2], x[j+1], height[j/2]);
    energia_o *= (2.0/(n*(n-1)));
    cout << "energia_o: " << energia_o << endl;
    return (1-alpha)*energia_o;
}

vector<double> read_elems()
{
    double x = 0.0, y = 0.0, w = 0.0, h = 0.0;
    vector<double> elems;
    ifstream ifs("points.rect");

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
        elems.push_back(x);
        ifs >> alpha;
        alpha = 0;
        cout << "alpha: " << alpha << endl;
        originais = vector<double>(elems.begin(), elems.end());
    }

    return elems;
}

vector<vector<double> > read_matrix() {

    vector<vector<double> > m;

    ifstream ifs("matrixL.matrix");

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

int main() {

    vector<double> x = read_elems();

    for( int i = 0; i < x.size(); ++i )
        cout << x[i] << endl;

    cout << "-----------------------------------------" << endl;
    L = read_matrix();

   /* for( int i = 0; i < L.size(); ++i ) {
        for( int j = 0; j < L.size(); ++j ) {
            if( L[i][j] != 0 && i != j )
                L[j][i] = L[i][j];
            else
                L[i][j] = L[j][i];
        }
    }*/

    valencia = vector<double>(L.size(), 0);
    for( int i = 0; i < L.size(); ++i ) {
        for( int j = 0; j < L.size(); ++j )
            if( L[i][j] && i != j )
            valencia[i]++;
    }

    int n = x.size();
    cout << "Dimencoes: " << n << endl;
	nlopt::opt opt(nlopt::LD_MMA, n);
    vector<double> lb(n, 0);
    vector<double> up(n, 800);
    opt.set_lower_bounds(lb);
    opt.set_upper_bounds(up);

    opt.set_min_objective(objective_function, NULL);

    opt.set_xtol_rel(1e-5);

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
        ofs << x.size()/2 << endl;
        for( int i = 0; i < x.size()-1; i+=2 ) {
            ofs << x[i] << " " << x[i+1] << " " << height[i/2] << " " << width[i/2] << endl;
        }
        ofs << x[x.size()-1];
    }





	return 0;
}


