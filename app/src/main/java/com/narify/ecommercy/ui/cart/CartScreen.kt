package com.narify.ecommercy.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.narify.ecommercy.R
import com.narify.ecommercy.data.cart.fake.CartFakeDataSource
import com.narify.ecommercy.model.CartItem
import com.narify.ecommercy.model.totalPriceText
import com.narify.ecommercy.ui.EmptyContent
import com.narify.ecommercy.ui.common.DevicePreviews
import com.narify.ecommercy.ui.common.LoadingContent
import com.narify.ecommercy.ui.theme.EcommercyThemePreview

@Composable
fun CartRoute(
    onCheckoutClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) LoadingContent()
    else if (uiState.errorState.hasError) EmptyContent(uiState.errorState.errorMsgResId)
    else if (uiState.cartItems.isEmpty()) EmptyContent(R.string.empty_cart)
    else CartScreen(
        cartItems = uiState.cartItems,
        onIncrementItem = { viewModel.increaseItemCount(it.product) },
        onDecrementItem = { viewModel.decreaseItemCount(it.product.id) },
        onCheckoutClicked = onCheckoutClicked,
        modifier = modifier
    )
}

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onIncrementItem: (CartItem) -> Unit,
    onDecrementItem: (CartItem) -> Unit,
    onCheckoutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(cartItems) { item ->
            CartItem(
                cartItemState = item,
                onIncrementItem = onIncrementItem,
                onDecrementItem = onDecrementItem,
            )
        }

        item {
            Button(
                onClick = onCheckoutClicked,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(40.dp)
                    .fillMaxWidth()
            ) {
                Text(stringResource(R.string.proceed_to_checkout))
            }
        }
    }
}

@Composable
fun CartItem(
    cartItemState: CartItem,
    onIncrementItem: (CartItem) -> Unit,
    onDecrementItem: (CartItem) -> Unit,
    modifier: Modifier = Modifier,
    cardColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = cardColor,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier
                .height(150.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = cartItemState.product.thumbnail,
                placeholder = painterResource(R.drawable.sample_product_item),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier.weight(1.2f)
            )
            Column(
                modifier
                    .padding(16.dp)
                    .weight(2f)
            ) {
                Text(
                    text = cartItemState.product.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = cartItemState.totalPriceText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Button(
                        shape = MaterialTheme.shapes.small,
                        onClick = { onDecrementItem(cartItemState) },
                    ) {
                        Text(
                            text = "-",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${cartItemState.count}",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Button(
                        shape = MaterialTheme.shapes.small,
                        onClick = { onIncrementItem(cartItemState) }
                    ) {
                        Text(
                            text = "+",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
fun CartScreenPreview() {
    EcommercyThemePreview {
        val cartItems = CartFakeDataSource().getPreviewCartItems().subList(0, 2)
        CartScreen(cartItems, { }, { }, { })
    }
}
