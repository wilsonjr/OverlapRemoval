<<<<<<< HEAD
# OverlapRemoval

A library to select sampling sets and remove overlapping in 2D projections.

## Techniques

* **RWordle**:  Strobelt M, Spicker M, Stoffel A et al. Rolled-out wordles: A heuristic method for overlap removal of 2d data representatives. Computer Graphics Forum 2012; : 1135–1144.

	* **RWordle-C**

	* **RWordle-L**

* **PRISM**:  Gansner ER and Hu Y. Efficient, proximity-preserving node overlap removal. Journal of Graph Algorithms and Applications 2010; 14(1): 53–74.

* **ProjSnippet**: G-Nieto E, Roman FS, Pagliosa P et al. Similarity preserving snippet-based visualization of web search results. IEEE Transactions on Visualization and Computer Graphics 2014; 20(3): 457–470

* **VPSC**: Dwyer T, Marriott K and Stuckey PJ. Fast node overlap removal. Proceedings of the 13th International Conference on Graph Drawing 2006; : 153–164.

* **HexBoard**: Pinho R and Oliveira MCF. Hexboard: Conveying pairwise similarity in an incremental visualization space. 13th Internation Conference Information Visualization 2009; : 32–37

* **IncBoard**: Pinho R, Oliveira MCF and Lopes AA. Incremental board: a grid-based space for visualizing dynamic data sets. Proceeding of the 2009 ACM symposium on Applied Computing 2009; 1757–1764

## Dependencies

We've used the following dependencies in the project:

**DT1.2**: [Delaunay Triangulation](http://doc.jzy3d.org/javadoc/0.8.4/org/jzy3d/plot3d/builder/delaunay/jdt/Delaunay_Triangulation.html)

**MLlib**: [Apache Spark's scalable machine learning library](http://spark.apache.org/mllib/)

**HexBoard**: [HexBoard/IncBoard](https://github.com/robertodepinho/HexBoard-API)

**commons-math3-3.6.jar**: [Commons Math](http://commons.apache.org/proper/commons-math/)

**Jama**: [Jama](http://math.nist.gov/javanumerics/jama/)

**EJML**: [EJML](https://ejml.org/wiki/index.php?title=Main_Page)
=======
# OverlapRemoval: a library for reducing overlap in 2D visualizations

Overplotting is a serious concern of everyone working with visualization. In this library, there are a few techniques that address the problem of overlapping markers in 2D projections. The techniques are designed to reduce overlapping while preserving the relationship among data instances.

## Techniques

* **RWordle-C and RWordle-L**:  Strobelt M, Spicker M, Stoffel A et al. Rolled-out wordles: A heuristic method for overlap removal of 2d data representatives. Computer Graphics Forum 2012; : 1135–1144.

* **PRISM**:  Gansner ER and Hu Y. Efficient, proximity-preserving node overlap removal. Journal of Graph Algorithms and Applications 2010; 14(1): 53–74.

* **ProjSnippet**: G-Nieto E, Roman FS, Pagliosa P et al. Similarity preserving snippet-based visualization of web search results. IEEE Transactions on Visualization and Computer Graphics 2014; 20(3): 457–470

* **VPSC**: Dwyer T, Marriott K and Stuckey PJ. Fast node overlap removal. Proceedings of the 13th International Conference on Graph Drawing 2006; : 153–164.

* **HexBoard**: Pinho R and Oliveira MCF. Hexboard: Conveying pairwise similarity in an incremental visualization space. 13th Internation Conference Information Visualization 2009; : 32–37

* **IncBoard**: Pinho R, Oliveira MCF and Lopes AA. Incremental board: a grid-based space for visualizing dynamic data sets. Proceeding of the 2009 ACM symposium on Applied Computing 2009; 1757–1764

## Citation

If you use the code, please cite us:

```BibTex
@article{MarcilioJr2019_OverlapRemoval,
    author = {Wilson E Marcílio-Jr and Danilo M Eler and Rogério E Garcia and Ives R Venturini Pola},
    title ={Evaluation of approaches proposed to avoid overlap of markers in visualizations based on multidimensional projection techniques},
    journal = {Information Visualization},
    volume = {18},
    number = {4},
    pages = {426-438},
    year = {2019},
    doi = {10.1177/1473871619845093}
}
```


If you use the SADIRE sampling selection, please use the following citation:

```BibTex
@article{MarcilioJr2020_SADIRE,
    title={SADIRE: a context-preserving sampling technique for dimensionality reduction visualizations}, 
    author={Marcilio-Jr, W. E., Eler, D. M.}, 
    journal={J Vis}, 
    year={2020}, 
    volume={23}, 
    pages={999-1013} 
}
```


## Dependencies

We've used the following dependencies in the project:

**DT1.2**: [Delaunay Triangulation](http://doc.jzy3d.org/javadoc/0.8.4/org/jzy3d/plot3d/builder/delaunay/jdt/Delaunay_Triangulation.html)

**MLlib**: [Apache Spark's scalable machine learning library](http://spark.apache.org/mllib/)

**HexBoard**: [HexBoard/IncBoard](https://github.com/robertodepinho/HexBoard-API)

**commons-math3-3.6.jar**: [Commons Math](http://commons.apache.org/proper/commons-math/)

**Jama**: [Jama](http://math.nist.gov/javanumerics/jama/)

**EJML**: [EJML](https://ejml.org/wiki/index.php?title=Main_Page)
>>>>>>> b3bdc2b146aad7ed8004d576623b639b6ce557d5
