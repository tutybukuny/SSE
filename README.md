# SSE
## Author: Tran Ha Ngoc Thien


### Please read me first!!! Important information is here!!!

I. Project Contructor information
1. Environment
	- OS: this project was built on Window 10 Enterprise (due to the lack of linux/ubuntu on my personal computer)
	- IDE: intellij IDE 2018.3.1
	- Language: Java 8
	- JDK version: 1.8.0_172
2. Contructor
	- pom.xml: belong to maven, do not remove it!
	- app.config: configurations of application which built by this project
		+ model_path: set the path of file where trained model will be stored
		+ algorithm: set which algorithm will be used be application, default is **Reverse Index** algorithm, to use **BM25** just set this property to "bm25"
		+ const.k: k constant in BM25 fomula, default of it is 1.2
		+ const.b: b constant in BM25 fomula, default of it is 0.75
		+ reverse_index_improvement: set it to true to enable my improment in Reverse Index algorithm
	- .idea: configuration folder of intellij idea
	- src/main/java: source code of project
	- src/main/test/java: source code of unit test (I'm using junit for this project)

II. Project Conclusion
1. Algorithm implementations
	- Both **Reverse Index** and **BM25** are implemented in classes stored in _src/main/java/thienthn/core/algorithm_ and in this project I use only **unigram** for extracting words from a string.
	- I aimed to make **BM25** is a boosting option for **Reverse Index** but at last I built them separately to be more specific. Therefore you can see 2 small engines **BM25Engine** and **ReverseIndexEngine**. They are managed by the class **EnginManger**, this class has responsibility for training and loading model for those 2 engines.
	- **Reverse Index** (or **Inverted Index**) algorithm that I build using OR boolean logic when filtering the results. This boolean logic makes me have more result and sort them orderly base on the query. It might be better or worse than AND in some case however I chose it to find results flexibly.
2. Conclusion for Reverse Index and BM25 Algorithm
	- Bases on my implementation and the my results, these 2 algorithms show pretty well performance. Though **BM25**is expected to be better than **Reverse Index**, some cases **Reverse Index** show it's suit well (note that I have changed **Reverse Index** a little bit). For example if the query is _"quạt"_, the result on the top of **BM25** will be _"goi kham suc khoe tong quat healthy life tai phong kham tong quat singapore - viet nam"_ while the first rank of **Reverse Index** is _"quạt hút gió tản nhiệt laptop ice coorel k1"_. This case is easy to understand, because I find not only the accent results but also the non-accent, so the **BM25** sees 2 term _"quat"_ (_"quat"_ is non-accent word of _"quạt"_) in the result and put it on the top.
	- Because of using **unigram**, so if the query or the name of product is wrong spelling these both engines might ignore some products or rank them badly worse they might not found anything. Example, with the query _"đồng hồ"_, they put the result _"đđồng hồ nam mini focus dây thép 6 kim lịch ngày js-mf087  - đen - trắng"_ faraway from the top (luckly they found this one at least)
	- On most situations, **BM25** returns more related results than **Reverse Index** because of it ranking fomula while **Reverse Index** just finds which result has those term. Therefore the returned results of **Reverse Index** is sorted irrelevantly. 
	  However on my implementation of **Reverse Index**, I changed a little bit in the finding process (it is described on my source code). Hope this change would improve this algorithm in the good way (for me, it's better)
	- Additionally, I'm not apply **BM25** algorithm over all products that we have, I just excuse it on the results which contain one or more terms of query. This approach reduces wasted time of searching.
3. Enhancement In The Future
	- As I said on the first line of section 1, I use only **unigram**. So if we use **bigram** and **trigram** additionally, it could be better. And I think using **meaning word** as term would be the useful (we can use some libraries to do this such as **vntokenizer**)
	- Wrong spelling is needed to solve and the performance will be elevated.
	- We could use a database management system to improve our search time.