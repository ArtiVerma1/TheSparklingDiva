package com.shopify.shopifyapp.shopifyqueries

import com.shopify.buy3.Storefront
import com.shopify.graphql.support.ID

object Query {
    val shopDetails: Storefront.QueryRootQuery
        get() = Storefront.query { q ->
            q
                    .shop { shop ->
                        shop
                                .paymentSettings { pay -> pay.enabledPresentmentCurrencies().currencyCode() }
                    }

        }
    private val productDefinition: Storefront.ProductConnectionQueryDefinition
        get() = Storefront.ProductConnectionQueryDefinition { productdata ->
            productdata
                    .edges({ edges ->
                        edges
                                .cursor()
                                .node({ node ->
                                    node
                                            .title()
                                            .images({ img -> img.first(10) }, { imag ->
                                                imag.edges({ imgedge ->
                                                    imgedge
                                                            .node({ imgnode ->
                                                                imgnode
                                                                        .originalSrc()
                                                                        .transformedSrc({ t ->
                                                                            t
                                                                                    .maxWidth(600)
                                                                                    .maxHeight(600)
                                                                        }
                                                                        )
                                                            }
                                                            )
                                                }
                                                )
                                            }
                                            )
                                            .media({ m -> m.first(10) }, { me ->
                                                me.edges({ e ->
                                                    e.node({ n ->
                                                        n
                                                                .onModel3d({ md ->
                                                                    md
                                                                            .sources({ s -> s.url() })
                                                                            .previewImage({ p -> p.originalSrc() })
                                                                })
                                                    })
                                                })
                                            })
                                            .availableForSale()
                                            .descriptionHtml()
                                            .description()
                                            .variants({ args ->
                                                args
                                                        .first(120)
                                            }, { variant ->
                                                variant
                                                        .edges({ variantEdgeQuery ->
                                                            variantEdgeQuery
                                                                    .node({ productVariantQuery ->
                                                                        productVariantQuery
                                                                                .priceV2({ price -> price.amount().currencyCode() })
                                                                                .price()
                                                                                .presentmentPrices({ arg -> arg.first(25) }, { price -> price.edges({ e -> e.cursor().node({ n -> n.price({ p -> p.amount().currencyCode() }).compareAtPrice({ cp -> cp.amount().currencyCode() }) }) }) })
                                                                                .selectedOptions({ select -> select.name().value() })
                                                                                .compareAtPriceV2({ compare -> compare.amount().currencyCode() })
                                                                                .compareAtPrice()
                                                                                .image({ image -> image.transformedSrc({ tr -> tr.maxHeight(600).maxWidth(600) }).originalSrc() })
                                                                                .availableForSale()
                                                                    }
                                                                    )
                                                        }
                                                        )
                                            }
                                            )
                                            .onlineStoreUrl()
                                            .options({ op ->
                                                op.name()
                                                        .values()
                                            }
                                            )
                                })
                    }
                    )
                    .pageInfo(Storefront.PageInfoQueryDefinition { it.hasNextPage() }
                    )
        }
    private val productQuery: Storefront.ProductQueryDefinition
        get() = Storefront.ProductQueryDefinition { product ->
            product
                    .title()
                    .images({ img -> img.first(10) }, { imag ->
                        imag.edges({ imgedge ->
                            imgedge.node({ imgnode ->
                                imgnode.originalSrc()
                                        .transformedSrc({ tr -> tr.maxWidth(600).maxHeight(600) })
                            }
                            )
                        }
                        )
                    }
                    )
                    .availableForSale()
                    .descriptionHtml()
                    .description()
                    .variants({ args ->
                        args
                                .first(120)
                    }, { variant ->
                        variant
                                .edges({ variantEdgeQuery ->
                                    variantEdgeQuery
                                            .node({ productVariantQuery ->
                                                productVariantQuery
                                                        .priceV2({ p -> p.amount().currencyCode() })
                                                        .presentmentPrices({ a -> a.first(50) }, { pre -> pre.edges({ ed -> ed.node({ n -> n.price({ p -> p.currencyCode().amount() }).compareAtPrice({ cp -> cp.amount().currencyCode() }) }).cursor() }) })
                                                        .selectedOptions({ select -> select.name().value() })
                                                        .compareAtPriceV2({ c -> c.amount().currencyCode() })
                                                        .image(Storefront.ImageQueryDefinition { it.originalSrc().transformedSrc() })
                                                        .availableForSale()
                                            }
                                            )
                                }
                                )
                    }
                    )
                    .media({ m -> m.first(10) }, { me ->
                        me.edges({ e ->
                            e.node({ n ->
                                n.onModel3d({ md ->
                                    md
                                            .sources({ s -> s.url().format() })
                                            .previewImage({ p -> p.originalSrc() })
                                })
                            })
                        })
                    })
                    .onlineStoreUrl()
                    .options({ op ->
                        op.name()
                                .values()
                    }
                    )
        }

    fun getProductsById(cat_id: String?, cursor: String, sortby_key: Storefront.ProductCollectionSortKeys, direction: Boolean, number: Int): Storefront.QueryRootQuery {
        val definition: Storefront.CollectionQuery.ProductsArgumentsDefinition
        if (cursor == "nocursor") {
            definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args -> args.first(number).sortKey(sortby_key).reverse(direction) }
        } else {
            definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args -> args.first(number).after(cursor).sortKey(sortby_key).reverse(direction) }
        }
        return Storefront.query { root ->
            root
                    .node(ID(cat_id)
                    ) { rootnode ->
                        rootnode.onCollection { oncollection ->
                            oncollection
                                    .image { image ->
                                        image
                                                .originalSrc()
                                                .transformedSrc { tr ->
                                                    tr
                                                            .maxHeight(300)
                                                            .maxWidth(700)
                                                }
                                    }
                                    .products(definition, productDefinition
                                    )
                        }
                    }
        }
    }

    fun getProductsByHandle(handle: String, cursor: String, sortby_key: Storefront.ProductCollectionSortKeys, direction: Boolean, number: Int): Storefront.QueryRootQuery {
        val definition: Storefront.CollectionQuery.ProductsArgumentsDefinition
        if (cursor == "nocursor") {
            definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args -> args.first(number).sortKey(sortby_key).reverse(direction) }
        } else {
            definition = Storefront.CollectionQuery.ProductsArgumentsDefinition { args -> args.first(number).after(cursor).sortKey(sortby_key).reverse(direction) }
        }
        return Storefront.query { root -> root.collectionByHandle(handle) { collect -> collect.products(definition, productDefinition) } }
    }

    fun getAllProducts(cursor: String, sortby_key: Storefront.ProductSortKeys, direction: Boolean, number: Int): Storefront.QueryRootQuery {
        val shoppro: Storefront.QueryRootQuery.ProductsArgumentsDefinition
        if (cursor == "nocursor") {
            shoppro = Storefront.QueryRootQuery.ProductsArgumentsDefinition { args -> args.first(number).sortKey(sortby_key).reverse(direction) }
        } else {
            shoppro = Storefront.QueryRootQuery.ProductsArgumentsDefinition { args -> args.first(number).after(cursor).sortKey(sortby_key).reverse(direction) }
        }
        return Storefront.query { root -> root.products(shoppro, productDefinition) }
    }

    fun getCollections(cursor: String): Storefront.QueryRootQuery {
        val definition: Storefront.QueryRootQuery.CollectionsArgumentsDefinition
        if (cursor == "nocursor") {
            definition = Storefront.QueryRootQuery.CollectionsArgumentsDefinition { args -> args.first(250) }
        } else {
            definition = Storefront.QueryRootQuery.CollectionsArgumentsDefinition { args -> args.first(250).after(cursor) }
        }
        return Storefront.query { root ->
            root.collections(definition, collectiondef)
        }
    }

    private val collectiondef: Storefront.CollectionConnectionQueryDefinition
        get() = Storefront.CollectionConnectionQueryDefinition { collect ->
            collect
                    .edges({ edge ->
                        edge
                                .cursor()
                                .node({ node -> node.title().image({ image -> image.originalSrc().transformedSrc() }) })
                    })
                    .pageInfo(Storefront.PageInfoQueryDefinition { it.hasNextPage() })
        }

    fun getProductById(product_id: String): Storefront.QueryRootQuery {
        return Storefront.query { root -> root.node(ID(product_id)) { rootnode -> rootnode.onProduct(productQuery) } }
    }

    fun getProductByHandle(handle: String): Storefront.QueryRootQuery {
        return Storefront.query { root -> root.productByHandle(handle, productQuery) }
    }

    fun getSearchProducts(keyword: String, cursor: String): Storefront.QueryRootQuery {

        return Storefront.query { root ->
            root
                    .products(
                            //   Storefront.QueryRootQuery.ProductsArgumentsDefinition { args -> args.query(keyword).first(30).sortKey(Storefront.ProductSortKeys.BEST_SELLING).reverse(false) }, productDefinition)
                            Storefront.QueryRootQuery.ProductsArgumentsDefinition { args -> product_list(args, cursor).query(keyword).sortKey(Storefront.ProductSortKeys.BEST_SELLING).reverse(false) }, productDefinition)
        }
    }

    private fun product_list(args: Storefront.QueryRootQuery.ProductsArguments?, cursor: String): Storefront.QueryRootQuery.ProductsArguments {
        var defination: Storefront.QueryRootQuery.ProductsArguments? = null
        if (cursor == "nocursor") {
            defination = args!!.first(10)
        } else {
            defination = args!!.first(10).after(cursor)
        }
        return defination
    }

    fun getCustomerDetails(customeraccestoken: String): Storefront.QueryRootQuery {

        return Storefront.query { root ->
            root
                    .customer(customeraccestoken
                    ) { customerQuery ->
                        customerQuery
                                .firstName()
                                .lastName()
                                .email()
                    }
        }
    }

    fun getOrderList(accesstoken: String?, cursor: String): Storefront.QueryRootQuery {

        return Storefront.query { root ->
            root
                    .customer(accesstoken
                    ) { customer ->
                        customer
                                .orders({ args -> order_list(args, cursor) }, { order ->
                                    order
                                            .edges({ edge ->
                                                edge
                                                        .cursor()
                                                        .node({ ordernode ->
                                                            ordernode
                                                                    .customerUrl()
                                                                    .statusUrl()
                                                                    .name()
                                                                    .processedAt()
                                                                    .orderNumber()
                                                                    .lineItems({ arg -> arg.first(150) }, { item ->
                                                                        item
                                                                                .edges({ itemedge ->
                                                                                    itemedge
                                                                                            .node({ n ->
                                                                                                n.title().quantity().variant({ v ->
                                                                                                    v.priceV2({ p -> p.amount().currencyCode() })
                                                                                                            .selectedOptions({ select -> select.name().value() })
                                                                                                            .compareAtPriceV2({ c -> c.amount().currencyCode() })
                                                                                                            .image(Storefront.ImageQueryDefinition { it.originalSrc() })
                                                                                                })
                                                                                            }
                                                                                            )
                                                                                }
                                                                                )
                                                                    }
                                                                    )
                                                                    .totalTaxV2({ tt -> tt.amount().currencyCode() })
                                                                    .shippingAddress({ ship -> ship.address1().address2().firstName().lastName().country().city().phone().province().zip() })
                                                                    .totalPriceV2({ tp -> tp.amount().currencyCode() })
                                                                    .subtotalPriceV2({ st -> st.amount().currencyCode() })
                                                                    .totalShippingPriceV2({ tsp -> tsp.currencyCode().amount() })
                                                        }
                                                        )
                                            }
                                            )
                                            .pageInfo(Storefront.PageInfoQueryDefinition { it.hasNextPage() })
                                }
                                )
                    }
        }
    }

    private fun order_list(arg: Storefront.CustomerQuery.OrdersArguments, cursor: String): Storefront.CustomerQuery.OrdersArguments {
        val definition: Storefront.CustomerQuery.OrdersArguments
        if (cursor == "nocursor") {
            definition = arg!!.first(10)
        } else {
            definition = arg!!.first(10).after(cursor)
        }
        return definition
    }

    fun getAddressList(accesstoken: String?, cursor: String): Storefront.QueryRootQuery {
        return Storefront.query { root ->
            root
                    .customer(accesstoken) { customer ->
                        customer
                                .addresses({ arg -> address_list(arg, cursor) }, { address ->
                                    address
                                            .edges({ edge ->
                                                edge
                                                        .cursor()
                                                        .node({ node ->
                                                            node
                                                                    .firstName().lastName().company().address1().address2().city().country().province().phone().zip().formattedArea()
                                                        }
                                                        )
                                            }
                                            )
                                            .pageInfo(Storefront.PageInfoQueryDefinition { it.hasNextPage() })
                                })
                    }
        }
    }

    private fun address_list(arg: Storefront.CustomerQuery.AddressesArguments, cursor: String): Storefront.CustomerQuery.AddressesArguments {
        val definitions: Storefront.CustomerQuery.AddressesArguments
        if (cursor == "nocursor")
            definitions = arg!!.first(10)
        else
            definitions = arg!!.first(10).after(cursor)

        return definitions
    }

    fun getProductByBarcode(barcode: String): Storefront.QueryRootQuery {
        return Storefront.query { root ->
            root
                    .products(
                            Storefront.QueryRootQuery.ProductsArgumentsDefinition { args -> args.query(barcode).first(1).sortKey(Storefront.ProductSortKeys.BEST_SELLING).reverse(false) }, productDefinition)
        }
    }

}
