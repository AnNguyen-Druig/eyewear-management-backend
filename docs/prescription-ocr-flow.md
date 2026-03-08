# Prescription OCR Flow

## Backend API

- Endpoint: `POST /api/prescriptions/parse-image`
- Content-Type: `multipart/form-data`
- Field: `file`

Response:

```json
{
  "code": 1000,
  "message": "Prescription parsed successfully",
  "result": {
    "rightEyeSph": "-1.25",
    "rightEyeCyl": "-0.5",
    "rightEyeAxis": "80",
    "rightEyeAdd": "2",
    "leftEyeSph": "-0.5",
    "leftEyeCyl": "-1.5",
    "leftEyeAxis": "120",
    "leftEyeAdd": "2",
    "pd": "132",
    "pdRight": "68",
    "pdLeft": "64",
    "confidence": 0.9,
    "requiresReview": true,
    "warnings": [
      "Không nhận diện được PD rõ ràng."
    ],
    "rawText": "..."
  }
}
```

## Frontend ProductDetail Integration (React Hook Form)

```tsx
import { useForm } from "react-hook-form";
import { useState } from "react";

type PrescriptionForm = {
  rightEyeSph: string;
  rightEyeCyl: string;
  rightEyeAxis: string;
  rightEyeAdd: string;
  leftEyeSph: string;
  leftEyeCyl: string;
  leftEyeAxis: string;
  leftEyeAdd: string;
  pd: string;
  pdRight: string;
  pdLeft: string;
};

type OcrResult = PrescriptionForm & {
  confidence: number;
  requiresReview: boolean;
  warnings: string[];
  rawText: string;
};

export default function ProductDetailPrescription() {
  const { register, setValue } = useForm<PrescriptionForm>();
  const [ocr, setOcr] = useState<OcrResult | null>(null);
  const [loading, setLoading] = useState(false);

  const applyResult = (r: OcrResult) => {
    setValue("rightEyeSph", r.rightEyeSph || "");
    setValue("rightEyeCyl", r.rightEyeCyl || "");
    setValue("rightEyeAxis", r.rightEyeAxis || "");
    setValue("rightEyeAdd", r.rightEyeAdd || "");
    setValue("leftEyeSph", r.leftEyeSph || "");
    setValue("leftEyeCyl", r.leftEyeCyl || "");
    setValue("leftEyeAxis", r.leftEyeAxis || "");
    setValue("leftEyeAdd", r.leftEyeAdd || "");
    setValue("pd", r.pd || "");
    setValue("pdRight", r.pdRight || "");
    setValue("pdLeft", r.pdLeft || "");
  };

  const onUpload = async (file: File) => {
    const formData = new FormData();
    formData.append("file", file);
    setLoading(true);
    try {
      const res = await fetch("/api/prescriptions/parse-image", {
        method: "POST",
        body: formData
      });
      const data = await res.json();
      const result: OcrResult = data.result;
      applyResult(result);
      setOcr(result);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <label>
        Tải ảnh đơn kính
        <input
          type="file"
          accept="image/*"
          onChange={(e) => {
            const file = e.target.files?.[0];
            if (file) onUpload(file);
          }}
        />
      </label>

      {ocr && (
        <div>
          <div>Độ tin cậy: {(ocr.confidence * 100).toFixed(0)}%</div>
          {ocr.requiresReview && <div>Cần kiểm tra và chỉnh tay trước khi thêm giỏ</div>}
          {ocr.warnings?.map((w) => (
            <div key={w}>{w}</div>
          ))}
        </div>
      )}

      <input {...register("leftEyeSph")} />
      <input {...register("leftEyeCyl")} />
      <input {...register("leftEyeAxis")} />
      <input {...register("leftEyeAdd")} />
      <input {...register("rightEyeSph")} />
      <input {...register("rightEyeCyl")} />
      <input {...register("rightEyeAxis")} />
      <input {...register("rightEyeAdd")} />
      <input {...register("pd")} />
      <input {...register("pdRight")} />
      <input {...register("pdLeft")} />
    </div>
  );
}
```

## Formik Mapping

```ts
setFieldValue("rightEyeSph", result.rightEyeSph || "");
setFieldValue("rightEyeCyl", result.rightEyeCyl || "");
setFieldValue("rightEyeAxis", result.rightEyeAxis || "");
setFieldValue("rightEyeAdd", result.rightEyeAdd || "");
setFieldValue("leftEyeSph", result.leftEyeSph || "");
setFieldValue("leftEyeCyl", result.leftEyeCyl || "");
setFieldValue("leftEyeAxis", result.leftEyeAxis || "");
setFieldValue("leftEyeAdd", result.leftEyeAdd || "");
setFieldValue("pd", result.pd || "");
setFieldValue("pdRight", result.pdRight || "");
setFieldValue("pdLeft", result.pdLeft || "");
```
