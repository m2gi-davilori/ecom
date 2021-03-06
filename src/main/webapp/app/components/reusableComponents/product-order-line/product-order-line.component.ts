import { Component, Input } from '@angular/core';
import { IProductCart } from '../../../entities/product-cart/product-cart.model';
import { WeightUnit } from '../../../entities/enumerations/weight-unit.model';
import { getPriceWeightStr } from '../../products/products.module';
import { IProduct } from '../../../entities/product/product.model';
import { PromotionService } from '../../services/promotion.service';
import { OrderService } from '../../services/order.service';
import { IProductOrder } from '../../../entities/product-order/product-order.model';

@Component({
  selector: 'jhi-product-order-line',
  templateUrl: './product-order-line.component.html',
})
export class ProductOrderLineComponent {
  @Input() productOrder: IProductOrder | null = null;

  constructor(public orderService: OrderService, public promotionService: PromotionService) {
    // do nothing.
  }

  unitOfPrice(weightUnit: WeightUnit): string {
    switch (weightUnit) {
      case WeightUnit.KG:
        return 'kilo';
      case WeightUnit.G:
        return 'kilo';
      case WeightUnit.L:
        return 'litre';
      case WeightUnit.ML:
        return 'litre';
      default:
        return '';
    }
  }

  getStringWeight(product?: IProduct): string {
    if (product!.weightUnit === WeightUnit.ML || product!.weightUnit === WeightUnit.L) {
      return 'L';
    } else if (product!.weightUnit === WeightUnit.G || product!.weightUnit === WeightUnit.KG) {
      return 'kg';
    } else {
      return 'u';
    }
  }

  getPriceWeightStrLine(product: IProduct): string {
    return getPriceWeightStr(product.price!, product.weight!, product.weightUnit!);
  }

  getPrice(productCart: IProductCart): number {
    if (this.promotionService.inPromotion(productCart.product!)) {
      const promo = this.promotionService.getPromotion(productCart.product!);
      const subPromo = Number(promo.substr(1, promo.length - 2));
      if (promo.substr(promo.length - 1) === '%') {
        return productCart.product!.price! - (productCart.product!.price! * subPromo) / 100;
      } else {
        return productCart.product!.price! - subPromo;
      }
    }
    return productCart.product!.price!;
  }

  getPriceWeightStrCardPromo(promo: any, product: IProduct): string {
    const res = Number(getPriceWeightStr(product.price!, product.weight!, product.weightUnit!).replace(',', '.'));
    const subPromo = Number(promo.substr(1, promo.length - 2));
    if (promo.substr(promo.length - 1) === '%') {
      return (res - (res * subPromo) / 100).toFixed(2).toString().replace('.', ',');
    } else {
      return (res - subPromo).toFixed(2).toString().replace('.', ',');
    }
  }
}
