/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.methods.utils;

/**
 *
 * @author wilson
 */
public class Intersector {
    
    private double pointx;
    private double pointy;

    private int codigoP1 = 0;
    private int codigoP2 = 0;
    private int flag = 2;

    private double x1_rect; 
    private double x2_rect; 
    private double y1_rect; 
    private double y2_rect;
    private double x1_reta; 
    private double x2_reta; 
    private double y1_reta; 
    private double y2_reta;
    
    
    public Intersector(double ux, double uy, double lx, double ly, double pointx, double pointy) {
            this.pointx = pointx;
            this.pointy = pointy;

            this.codigoP1 = 0;
            this.codigoP2 = 0;
            this.flag = 2;

            this.x1_rect = ux; this.x2_rect = lx; this.y1_rect = uy; this.y2_rect = ly;
            this.x1_reta = pointx; 
            this.x2_reta = pointx; 
            this.y1_reta = pointy;
            this.y2_reta = pointy;
    }

    public boolean isInside(double x, double y) {
            return x >= this.x1_rect && x <= this.x2_rect && y >= this.y1_rect && y <= this.y2_rect;
    }

    public void mouseDown(double x, double y) {
            if( flag < 2 ) {
                    this.x1_rect = x;
                    this.y1_rect = y;
                    flag++;
            } else {
                    this.x1_reta = x;
                    this.y1_reta = y;
            }
    }

    public boolean checkIn(int codigo) {

            for( int i = 3; i >= 0; --i )
                    if( (codigo & (1 << i)) != 0 )
                        return false;

            return true;
    }
    
    public int setCodigo(double x, double y) {
            int codigo = 0;

            if( x < Math.min(this.x1_rect, this.x2_rect) ) {
                    codigo |= (1);
            } else if( x > Math.max(this.x1_rect, this.x2_rect) ) {
                    codigo |= (1 << 1);
            }

            if( y < Math.min(this.y1_rect, this.y2_rect) ) {
                    codigo |= (1 << 3);
            } else if( y > Math.max(this.y1_rect, this.y2_rect) ) {
                    codigo |= (1 << 2);
            }

            return codigo;		
    }

    public double[] mouseUp(double X, double Y) {

            if( this.flag < 2 ) { // tirar isso aqui (era para normalizar os pontos)
                    this.x2_rect = X;
                    this.y2_rect = Y;
                    this.flag++;

            } else {
                    this.codigoP2 = 0;
                    this.codigoP1 = 0;
                    this.x2_reta = X;
                    this.y2_reta = Y;

                    this.codigoP2 = this.setCodigo(this.x2_reta, this.y2_reta);
                    this.codigoP1 = this.setCodigo(this.x1_reta, this.y1_reta);
                    if( (this.codigoP1 & this.codigoP2) == 0) {
                            double m = 0;

                            double xl = Math.min(this.x1_rect, this.x2_rect), xr = Math.max(this.x1_rect, this.x2_rect),
                                    yt = Math.min(this.y1_rect, this.y2_rect), yb = Math.max(this.y1_rect, this.y2_rect);

                            double p1x = -1, p1y = -1, p2x = -1, p2y = -1;

                            if( this.checkIn(this.codigoP1) && this.checkIn(this.codigoP2) ) {
                                    return new double[]{this.x1_reta, this.y1_reta, this.x2_reta, this.y2_reta};

                            } else if( this.checkIn(this.codigoP1) || this.checkIn(this.codigoP2) ) {
                                    /**
                                    * Entra caso apenas um extremo da reta esteja dentro da área de recorte
                                    */
                                    if( this.checkIn(this.codigoP1) ) {
                                            // esta a esquerda?
                                            if( (this.codigoP2 & (1 << 0)) != 0 ) {
                                                    m = (this.y2_reta - this.y1_reta)/(this.x2_reta - this.x1_reta);

                                                    if( m != 0 ) {
                                                            if( m*(xl-this.x1_reta)+this.y1_reta <= yb &&
                                                                    m*(xl-this.x1_reta)+this.y1_reta >= yt )
                                                            {
                                                                    p1x = xl;
                                                                    p1y = m*(xl-this.x1_reta)+this.y1_reta;
                                                            }
                                                            else
                                                            {
                                                                    if( (this.codigoP2 & (1 << 3)) != 0 )
                                                                    {
                                                                            p1y = yt;
                                                                            p1x = this.x1_reta+(1./m)*(yt-this.y1_reta);
                                                                    }
                                                                    else
                                                                    {
                                                                            p1y = yb;
                                                                            p1x = this.x1_reta+(1./m)*(yb-this.y1_reta);
                                                                    }
                                                            }
                                                            p2x = this.x1_reta;
                                                            p2y = this.y1_reta;
                                                    }
                                                    else
                                                    {
                                                            p1x = xl;
                                                            p2x = this.x1_reta;
                                                            p1y = p2y = this.y1_reta;
                                                    }
                                            }
                                            else if( (this.codigoP2 & (1 << 1)) != 0 )// está a direita?
                                            {
                                                   m = (this.y2_reta - this.y1_reta)/(this.x2_reta - this.x1_reta);

                                                    if( m != 0 )
                                                    {
                                                            if( m*(xr-this.x1_reta)+this.y1_reta <= yb &&
                                                                    m*(xr-this.x1_reta)+this.y1_reta >= yt )
                                                            {
                                                                    p1x = xr;
                                                                    p1y = m*(xr-this.x1_reta)+this.y1_reta;
                                                            }
                                                            else
                                                            {
                                                                    if( (this.codigoP2 & (1 << 3)) != 0 )
                                                                    {
                                                                            p1y = yt;
                                                                            p1x = this.x1_reta+(1./m)*(yt-this.y1_reta);
                                                                    }
                                                                    else
                                                                    {
                                                                            p1y = yb;
                                                                            p1x = this.x1_reta+(1./m)*(yb-this.y1_reta);
                                                                    }
                                                            }
                                                            p2x = this.x1_reta;
                                                            p2y = this.y1_reta;
                                                    }
                                                    else
                                                    {
                                                            p1x = xr;
                                                            p2x = this.x1_reta;
                                                            p1y = p2y = this.y1_reta;
                                                    }
                                            }
                                            else if( (this.codigoP2 & (1 << 2)) != 0 ) // está abaixo
                                            {
                                                    if( this.x2_reta != this.x1_reta ) {
                                                            m = (this.y2_reta - this.y1_reta)/(this.x2_reta - this.x1_reta);

                                                            if( this.x1_reta+(1./m)*(yb-this.y1_reta) >= xl &&
                                                                    this.x1_reta+(1./m)*(yb-this.y1_reta) <= xr )
                                                            {
                                                                    p1x = this.x1_reta+(1./m)*(yb-this.y1_reta);
                                                                    p1y = yb;
                                                                    p2y = this.y1_reta;
                                                                    p2x = this.x1_reta;
                                                            }
                                                    } else {
                                                            p1y = yb;
                                                            p2x = p1x = this.x1_reta;
                                                            p2y = this.y1_reta;
                                                    }
                                            } else // está acima
                                            {
                                               
                                                    if( this.x2_reta != this.x1_reta ) {
                                                            m = (this.y2_reta - this.y1_reta)/(this.x2_reta - this.x1_reta);

                                                            if( this.x1_reta+(1./m)*(yt-this.y1_reta) >= xl &&
                                                                    this.x1_reta+(1./m)*(yt-this.y1_reta) <= xr ) {
                                                                    p1x = this.x1_reta+(1./m)*(yt-this.y1_reta);
                                                                    p1y = yt;
                                                                    p2y = this.y1_reta;
                                                                    p2x = this.x1_reta;
                                                            }
                                                    } else {
                                                            p1y = yt;
                                                            p2x = p1x = this.x1_reta;
                                                            p2y = this.y1_reta;
                                                    }

                                            }

                                            return new double[]{p1x, p1y, p2x, p2y};
                                    } else {
                        // esta a esquerda?
                                            if( (this.codigoP1 & (1 << 0)) != 0 )
                                            {
                                                    m = (this.y2_reta - this.y1_reta)/(this.x2_reta - this.x1_reta);

                                                    if( m != 0 )
                                                    {
                                                            if( m*(xl-this.x2_reta)+this.y2_reta <= yb &&
                                                                    m*(xl-this.x2_reta)+this.y2_reta >= yt )
                                                            {
                                                                    p1x = xl;
                                                                    p1y = m*(xl-this.x2_reta)+this.y2_reta;
                                                            }
                                                            else
                                                            {
                                                                    if( (this.codigoP1 & (1 << 3)) != 0 )
                                                                    {
                                                                            p1y = yt;
                                                                            p1x = this.x2_reta+(1./m)*(yt-this.y2_reta);
                                                                    }
                                                                    else
                                                                    {
                                                                            p1y = yb;
                                                                            p1x = this.x2_reta+(1./m)*(yb-this.y2_reta);
                                                                    }
                                                            }
                                                            p2x = this.x2_reta;
                                                            p2y = this.y2_reta;
                                                    }
                                                    else
                                                    {
                                                            p1x = xl;
                                                            p2x = this.x2_reta;
                                                            p1y = p2y = this.y2_reta;
                                                    }
                                            }
                                            else if( (this.codigoP1 & (1 << 1)) != 0 )// está a direita?
                                            {
                                                    m = (this.y2_reta - this.y1_reta)/(this.x2_reta - this.x1_reta);

                                                    if( m != 0 )
                                                    {
                                                            if( m*(xr-this.x2_reta)+this.y2_reta <= yb &&
                                                                    m*(xr-this.x2_reta)+this.y2_reta >= yt )
                                                            {
                                                                    p1x = xr;
                                                                    p1y = m*(xr-this.x2_reta)+this.y2_reta;
                                                            }
                                                            else
                                                            {

                                                                    if( (this.codigoP1 & (1 << 3)) != 0 )
                                                                    {
                                                                            p1y = yt;
                                                                            p1x = this.x2_reta+(1./m)*(yt-this.y2_reta);
                                                                    }
                                                                    else
                                                                    {
                                                                            p1y = yb;
                                                                            p1x = this.x2_reta+(1./m)*(yb-this.y2_reta);
                                                                    }
                                                            }
                                                            p2x = this.x2_reta;
                                                            p2y = this.y2_reta;
                                                    }
                                                    else
                                                    {
                                                            p1x = xr;
                                                            p2x = this.x2_reta;
                                                            p1y = p2y = this.y2_reta;
                                                    }
                                            }
                                            else if( (this.codigoP1 & (1 << 2)) != 0 ) // está abaixo
                                            {
                                                    if( this.x2_reta != this.x1_reta ) {
                                                            m = (this.y2_reta - this.y1_reta)/(this.x2_reta - this.x1_reta);

                                                            if( this.x2_reta+(1./m)*(yb-this.y2_reta) >= xl &&
                                                                    this.x2_reta+(1./m)*(yb-this.y2_reta) <= xr )
                                                            {
                                                                    p1x = this.x2_reta+(1./m)*(yb-this.y2_reta);
                                                                    p1y = yb;
                                                                    p2y = this.y2_reta;
                                                                    p2x = this.x2_reta;
                                                            }
                                                    } else {
                                                            p1y = yb;
                                                            p2x = p1x = this.x2_reta;
                                                            p2y = this.y2_reta;
                                                    }
                                            } else // está acima
                                            {
                                                     if( this.x2_reta != this.x1_reta ) {
                                                            m = (this.y2_reta - this.y1_reta)/(this.x2_reta - this.x1_reta);

                                                            if( this.x2_reta+(1./m)*(yt-this.y2_reta) >= xl &&
                                                                    this.x2_reta+(1./m)*(yt-this.y2_reta) <= xr ) {
                                                                    p1x = this.x2_reta+(1./m)*(yt-this.y2_reta);
                                                                    p1y = yt;
                                                                    p2y = this.y2_reta;
                                                                    p2x = this.x2_reta;
                                                            }
                                                    } else {
                                                            p1y = yt;
                                                            p2x = p1x = this.x2_reta;
                                                            p2y = this.y2_reta;
                                                    }

                                            }
                                            return new double[]{p1x, p1y, p2x, p2y};
                                    }

                            } else {

                                    if( this.x2_reta != this.x1_reta ) {
                                            m = (this.y2_reta - this.y1_reta)/(this.x2_reta - this.x1_reta);

                                            if( m*(xl-this.x1_reta)+this.y1_reta <= yb && m*(xl-this.x1_reta)+this.y1_reta >= yt ) {
                                                    p1x = xl;
                                                    p1y = m*(xl-this.x1_reta)+this.y1_reta;
                                            }

                                            if( m*(xr-this.x1_reta)+this.y1_reta <= yb && m*(xr-this.x1_reta)+this.y1_reta >= yt ) {
                                                    if( p1x == -1 ) {
                                                            p1x = xr;
                                                            p1y = m*(xr-this.x1_reta)+this.y1_reta;
                                                    } else {
                                                            p2x = xr;
                                                            p2y = m*(xr-this.x1_reta)+this.y1_reta;
                                                    }

                                            }
                                            if( m != 0 ) {
                                                    if( this.x1_reta+(1.0/m)*(yt-this.y1_reta) >= xl && this.x1_reta+(1./m)*(yt-this.y1_reta) <= xr ) {
                                                            p2x = this.x1_reta+(1./m)*(yt-this.y1_reta);
                                                            p2y = yt;
                                                    }

                                                    if( this.x1_reta+(1.0/m)*(yb-this.y1_reta) >= xl && this.x1_reta+(1./m)*(yb-this.y1_reta) <= xr ) {
                                                            if( p2x == -1 ) {
                                                                    p2x = this.x1_reta+(1./m)*(yb-this.y1_reta);
                                                                    p2y = yb;
                                                            } else {
                                                                    p1x = this.x1_reta+(1./m)*(yb-this.y1_reta);
                                                                    p1y = yb;
                                                            }
                                                    }
                                            } else {
                                                    p1y = p2y = this.y1_reta;
                                                    p1x = xl;
                                                    p2x = xr;
                                            }
                                    } else {
                                            p1x = p2x = this.x1_reta;
                                            p1y = yt;
                                            p2y = yb;


                                    }

                                    return new double[]{p1x, p1y, p2x, p2y};
                            }
                    }
            }
            
            return null;

    }
    
}
